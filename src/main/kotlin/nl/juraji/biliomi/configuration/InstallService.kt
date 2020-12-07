package nl.juraji.biliomi.configuration

import nl.juraji.biliomi.configuration.security.Authorities
import nl.juraji.biliomi.domain.user.commands.AddUserToAuthorityGroupCommand
import nl.juraji.biliomi.domain.user.commands.CreateAuthorityGroupCommand
import nl.juraji.biliomi.domain.user.commands.CreateUserCommand
import nl.juraji.biliomi.utils.LoggerCompanion
import nl.juraji.biliomi.utils.extensions.uuidV4
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.kotlin.core.publisher.toFlux

@Configuration
class InstallService(
    private val commandGateway: ReactorCommandGateway,
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val passwordEncoder: PasswordEncoder,
) {

    @EventListener(ContextRefreshedEvent::class)
    fun init() {
        runInstallTask("installUsersGroup", this::installUsersGroup)
        runInstallTask("installAdministratorGroupAndUser", this::installAdminGroupAndUser)
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
                    groupId = uuidV4(),
                    groupName = usersGroupName,
                    authorities = usersGroupAuthorities,
                    default = true
                )
            )
            .doOnNext { logger.info("Created authority group \"$usersGroupName\" with id $it as default group") }
            .block()
    }

    private fun installAdminGroupAndUser() {
        val adminGroupName = "Administrators"
        val adminUsernamePassword = "admin"
        val adminDisplayName = "Administrator"

        val createAdminGroupCmd = CreateAuthorityGroupCommand(
            groupId = uuidV4(),
            groupName = adminGroupName,
            authorities = Authorities.all.toSet(),
            protected = true
        )

        val createAdminUserCmd = CreateUserCommand(
            username = adminUsernamePassword,
            displayName = adminDisplayName,
            passwordHash = passwordEncoder.encode(adminUsernamePassword)
        )

        val linkAdminGrpCmd = AddUserToAuthorityGroupCommand(
            username = createAdminUserCmd.username,
            groupId = createAdminGroupCmd.groupId
        )

        commandGateway
            .sendAll(
                arrayOf(
                    createAdminGroupCmd,
                    createAdminUserCmd,
                    linkAdminGrpCmd
                ).toFlux()
            )
            .blockLast()

        logger.info("Created authority group \"$adminGroupName\"")
        logger.info("""
            
            !!! Important !!!
            Created administrator user "$adminUsernamePassword" with password "$adminUsernamePassword".
            Use this account to log on to Biliomi. Also make sure to change the password!
            
        """.trimIndent())
    }

    companion object : LoggerCompanion(InstallService::class)
}
