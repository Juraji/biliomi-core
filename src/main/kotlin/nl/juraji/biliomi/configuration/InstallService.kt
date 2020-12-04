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
        val adminGroupName = "Administrators"
        val adminUsernamePassword = "admin"
        val adminDisplayName = "Administrator"

        val administratorGroupId: Mono<String> = commandGateway.send(
            CreateAuthorityGroupCommand(
                groupId = uuid(),
                groupName = adminGroupName,
                authorities = Authorities.all.toSet(),
                protected = true
            )
        )

        val administratorUsername: Mono<String> = commandGateway.send(
            CreateUserCommand(
                username = adminUsernamePassword,
                displayName = adminDisplayName,
                passwordHash = passwordEncoder.encode(adminUsernamePassword)
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
            .doOnNext { (username, groupId) ->
                logger.info("Created authority group \"$adminGroupName\" with id $groupId")
                logger.info("Created user \"$adminUsernamePassword\" with password \"$adminUsernamePassword\" with id" +
                        " $username and added it to group \"$adminGroupName\"")
            }
            .block()
    }

    private fun installUsersGroup() {
        val usersGroupName = "Users"
        val usersGroupAuthorities = setOf(
            Authorities.USERS_READ_ME,
            Authorities.USERS_UPDATE_ME_DISPLAY_NAME,
            Authorities.USERS_UPDATE_ME_PASSWORD,
            Authorities.BANK_READ_ME,
        )

        commandGateway
            .send<String>(
                CreateAuthorityGroupCommand(
                    groupId = uuid(),
                    groupName = usersGroupName,
                    authorities = usersGroupAuthorities
                )
            )
            .doOnNext { logger.info("Created authority group \"$usersGroupName\" with id $it ") }
            .block()
    }

    companion object : LoggerCompanion(InstallService::class)
}
