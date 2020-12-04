package nl.juraji.biliomi.domain.user

import nl.juraji.biliomi.domain.user.commands.*
import nl.juraji.biliomi.domain.user.events.*
import nl.juraji.reactor.validations.validate
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class UserAggregate() {

    @AggregateIdentifier
    private lateinit var userId: String
    private lateinit var username: String
    private var groupIds: Set<String> = emptySet()

    @CommandHandler
    constructor(cmd: CreateUserCommand) : this() {
        validate {
            isNotBlank(cmd.userId) { "User id may not be empty" }
            isNotBlank(cmd.username) { "Username may not be empty" }
            unless(cmd.passwordHash == null) {
                isNotBlank(cmd.passwordHash) { "Password hash may not be empty" }
            }
        }

        AggregateLifecycle.apply(
            UserCreatedEvent(
                userId = cmd.userId,
                username = cmd.username,
                passwordHash = cmd.passwordHash
            )
        )
    }

    @CommandHandler
    fun handle(cmd: AddUserToAuthorityGroupCommand) {
        validate {
            isFalse(groupIds.contains(cmd.groupId)) { "User $username is already in group with id ${cmd.groupId}" }
        }

        AggregateLifecycle.apply(
            UserAddedToAuthorityGroupEvent(
                userId = userId,
                groupId = cmd.groupId
            )
        )
    }

    @CommandHandler
    fun handle(cmd: RemoveUserFromAuthorityGroupCommand) {
        validate {
            isTrue(groupIds.contains(cmd.groupId)) { "User $username is not in group with id ${cmd.groupId}" }
        }

        AggregateLifecycle.apply(
            UserRemovedFromAuthorityGroupEvent(
                userId = userId,
                groupId = cmd.groupId
            )
        )
    }

    @CommandHandler
    fun handle(cmd: SetUserUsernameCommand) {
        validate {
            isNotBlank(cmd.username) { "Username may not be empty" }
        }

        AggregateLifecycle.apply(
            UserUsernameUpdatedEvent(
                userId = userId,
                username = cmd.username,
            )
        )
    }

    @CommandHandler
    fun handle(cmd: SetUserPasswordCommand) {
        validate {
            isNotBlank(cmd.passwordHash) { "Password hash may not be empty" }
        }

        AggregateLifecycle.apply(
            UserPasswordUpdatedEvent(
                userId = userId,
                passwordHash = cmd.passwordHash
            )
        )
    }

    @CommandHandler
    fun handle(cmd: DeleteUserCommand) {
        AggregateLifecycle.apply(UserDeletedEvent(userId = cmd.userId))
    }

    @EventSourcingHandler
    fun on(e: UserCreatedEvent) {
        this.userId = e.userId
        this.username = e.username
    }

    @EventSourcingHandler
    fun on(e: UserAddedToAuthorityGroupEvent) {
        groupIds = groupIds.plus(e.groupId)
    }

    @EventSourcingHandler
    fun on(e: UserRemovedFromAuthorityGroupEvent) {
        groupIds = groupIds.minus(e.groupId)
    }

    @EventSourcingHandler
    fun on(e: UserUsernameUpdatedEvent) {
        this.username = e.username
    }

    @EventSourcingHandler
    fun on(e: UserDeletedEvent) {
        AggregateLifecycle.markDeleted()
    }
}
