package nl.juraji.biliomi.domain.bank

import nl.juraji.biliomi.domain.bank.commands.AddBalanceCommand
import nl.juraji.biliomi.domain.bank.commands.CreateBankCommand
import nl.juraji.biliomi.domain.bank.commands.TakeBalanceCommand
import nl.juraji.biliomi.domain.bank.events.BalanceUpdatedEvent
import nl.juraji.biliomi.domain.bank.events.BankCreatedEvent
import nl.juraji.biliomi.utils.Validate
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class BankAggregate() {

    @AggregateIdentifier
    private lateinit var userId: String
    private var balance: Long = 0

    @CommandHandler
    constructor(cmd: CreateBankCommand) : this() {
        AggregateLifecycle.apply(BankCreatedEvent(userId = cmd.userId))
    }

    @CommandHandler
    fun handle(cmd: AddBalanceCommand) {
        Validate.isTrue(cmd.amount > 0) { "Amount must be larger than 0" }

        AggregateLifecycle.apply(BalanceUpdatedEvent(
                userId = userId,
                previousBalance = balance,
                newBalance = balance + cmd.amount
        ))
    }

    @CommandHandler
    fun handle(cmd: TakeBalanceCommand) {
        Validate.isTrue(cmd.amount > 0) { "Amount must be larger than 0" }
        Validate.isTrue(balance >= cmd.amount) { "Insufficient balance ($balance))" }

        AggregateLifecycle.apply(BalanceUpdatedEvent(
                userId = userId,
                previousBalance = balance,
                newBalance = balance - cmd.amount
        ))
    }

    @EventSourcingHandler
    fun on(e: BankCreatedEvent) {
        userId = e.userId
    }

    @EventSourcingHandler
    fun on(e: BalanceUpdatedEvent) {
        balance = e.newBalance
    }
}
