package nl.juraji.biliomi.configuration

import nl.juraji.biliomi.configuration.security.Authorities
import nl.juraji.biliomi.domain.user.commands.AddUserToAuthorityGroupCommand
import nl.juraji.biliomi.domain.user.commands.CreateAuthorityGroupCommand
import nl.juraji.biliomi.domain.user.commands.CreateUserCommand
import nl.juraji.biliomi.utils.LoggerCompanion
import nl.juraji.biliomi.utils.extensions.uuid
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2

@Configuration
class InstallService(
    private val commandGateway: ReactorCommandGateway,
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val passwordEncoder: PasswordEncoder,
) {

    @EventListener(ContextRefreshedEvent::class)
    fun init() {
        runInstallTask("installAdministratorGroupAndUser", this::installAdminGroupAndUser)
        runInstallTask("installUsersGroup", this::installUsersGroup)
    }

    private fun runInstallTask(taskName: String, taskRunner: () -> Unit) {
        val isTaskCompleted = jdbcTemplate.query(
            "select 1 from InstallServiceCompletedTasks where taskName = :taskName;",
            mapOf("taskName" to taskName),
            ResultSetExtractor { resultSet -> resultSet.next() }
        ) == true

        if (!isTaskCompleted) {
            logger.info("Running task: $taskName")
            taskRunner
                .runCatching { invoke() }
                .onSuccess {
                    jdbcTemplate.execute(
                        "insert into InstallServiceCompletedTasks (taskName) values (:taskName)",
                        mapOf("taskName" to taskName)
                    ) { stmt -> stmt.execute() }

                    logger.info("Completed task: $taskName")
                }
                .onFailure { ex ->
                    logger.error("Install task failed: $taskName", ex)
                }
        }
    }

    private fun installAdminGroupAndUser() {
        val administratorGroupId: Mono<String> = commandGateway.send(
            CreateAuthorityGroupCommand(
                groupId = uuid(),
                groupName = "Administrators",
                protected = true,
                authorities = Authorities.all.toSet()
            )
        )

        val administratorUsername: Mono<String> = commandGateway.send(
            CreateUserCommand(
                username = "admin",
                displayName = "Administrator",
                passwordHash = passwordEncoder.encode("admin")
            )
        )

        Mono.zip(administratorUsername, administratorGroupId)
            .flatMap { (username, groupId) ->
                commandGateway.send<Unit>(
                    AddUserToAuthorityGroupCommand(
                        username = username,
                        groupId = groupId
                    )
                ).then(Mono.just(username to groupId))
            }
            .doOnNext { (u, g) ->
                logger.info("Created authority group \"Administrators\" with id $g")
                logger.info("Created user \"admin\" with password \"admin\" with id $u and added it to group \"Administrators\"")
            }
            .block()
    }

    private fun installUsersGroup() {
        commandGateway
            .send<String>(
                CreateAuthorityGroupCommand(
                    groupId = uuid(),
                    groupName = "Users",
                    authorities = setOf(
                        Authorities.USERS_READ_ME,
                        Authorities.USERS_UPDATE_ME_USERNAME,
                        Authorities.USERS_UPDATE_ME_PASSWORD,
                        Authorities.BANK_READ_ME,
                    )
                )
            )
            .doOnNext { logger.info("Created authority group \"Users\" with id $it ") }
            .block()
    }

    companion object : LoggerCompanion(InstallService::class)
}
