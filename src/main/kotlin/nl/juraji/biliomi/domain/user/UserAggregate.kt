package nl.juraji.biliomi.domain.user

import nl.juraji.biliomi.domain.user.commands.AddPointsCommand
import nl.juraji.biliomi.domain.user.commands.CreateUserCommand
import nl.juraji.biliomi.domain.user.commands.SubtractPointsCommand
import nl.juraji.biliomi.domain.user.events.PointBalanceUpdatedEvent
import nl.juraji.biliomi.domain.user.events.UserCreatedEvent
import nl.juraji.biliomi.utils.Validate
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class UserAggregate() {

    @AggregateIdentifier
    private lateinit var userId: UserId
    private lateinit var username: String
    private var pointBalance: Long = 0

    @CommandHandler
    constructor(cmd: CreateUserCommand) : this() {
        Validate.isFalse(cmd.username.isBlank()) { "Username may not be blank" }

        AggregateLifecycle.apply(
                UserCreatedEvent(
                        userId = cmd.userId,
                        username = cmd.username,
                )
        )
    }

    @CommandHandler
    fun handle(cmd: AddPointsCommand) {
        Validate.isTrue(cmd.amount > 0) { "Amount to add should be a positive number" }

        AggregateLifecycle.apply(
                PointBalanceUpdatedEvent(
                        userId = userId,
                        previousBalance = pointBalance,
                        newBalance = pointBalance + cmd.amount
                )
        )
    }

    @CommandHandler
    fun handle(cmd: SubtractPointsCommand) {
        Validate.isTrue(cmd.amount > 0) { "Amount to subtract should be a positive number" }
        Validate.isTrue(pointBalance >= cmd.amount) { "Not able to subtract ${cmd.amount} when point balance is $pointBalance" }

        AggregateLifecycle.apply(
                PointBalanceUpdatedEvent(
                        userId = userId,
                        previousBalance = pointBalance,
                        newBalance = pointBalance - cmd.amount
                )
        )
    }

    @EventSourcingHandler
    fun on(e: UserCreatedEvent) {
        this.userId = e.userId
        this.username = e.username
    }

    @EventSourcingHandler
    fun on(e: PointBalanceUpdatedEvent) {
        this.pointBalance = e.newBalance
    }
}
