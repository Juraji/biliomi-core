package nl.juraji.biliomi.domain.user

import nl.juraji.biliomi.domain.user.commands.CreateUserCommand
import nl.juraji.biliomi.domain.user.commands.DeleteUserCommand
import nl.juraji.biliomi.domain.user.events.UserCreatedEvent
import nl.juraji.biliomi.domain.user.events.UserDeletedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class UserAggregate() {

    @AggregateIdentifier
    private lateinit var userId: String

    @CommandHandler
    constructor(cmd: CreateUserCommand) : this() {
        AggregateLifecycle.apply(UserCreatedEvent(userId = cmd.userId))
    }

    @CommandHandler
    fun handle(cmd: DeleteUserCommand) {
        AggregateLifecycle.apply(UserDeletedEvent(userId = cmd.userId))
    }

    @EventSourcingHandler
    fun on(e: UserCreatedEvent) {
        this.userId = e.userId
    }

    @EventSourcingHandler
    fun on(e: UserDeletedEvent) {
        AggregateLifecycle.markDeleted()
    }
}
