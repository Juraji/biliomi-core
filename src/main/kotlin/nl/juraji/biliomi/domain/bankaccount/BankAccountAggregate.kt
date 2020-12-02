package nl.juraji.biliomi.domain.bankaccount

import nl.juraji.biliomi.domain.bankaccount.commands.AddBankAccountBalanceCommand
import nl.juraji.biliomi.domain.bankaccount.commands.CreateBankAccountCommand
import nl.juraji.biliomi.domain.bankaccount.commands.DeleteBankAccountCommand
import nl.juraji.biliomi.domain.bankaccount.commands.TakeBankAccountBalanceCommand
import nl.juraji.biliomi.domain.bankaccount.events.BankAccountBalanceUpdatedEvent
import nl.juraji.biliomi.domain.bankaccount.events.BankAccountCreatedEvent
import nl.juraji.biliomi.domain.bankaccount.events.BankAccountDeletedEvent
import nl.juraji.reactor.validations.validate
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class BankAccountAggregate() {

    @AggregateIdentifier
    private lateinit var userId: String
    private var balance: Long = 0

    @CommandHandler
    constructor(cmd: CreateBankAccountCommand) : this() {
        AggregateLifecycle.apply(BankAccountCreatedEvent(userId = cmd.userId))
    }

    @CommandHandler
    fun handle(cmd: AddBankAccountBalanceCommand) {
        validate {
            isTrue(cmd.amount > 0) { "Amount must be larger than 0" }
        }

        AggregateLifecycle.apply(BankAccountBalanceUpdatedEvent(
                userId = userId,
                previousBalance = balance,
                newBalance = balance + cmd.amount
        ))
    }

    @CommandHandler
    fun handle(cmd: TakeBankAccountBalanceCommand) {
        validate {
            isTrue(cmd.amount > 0) { "Amount must be larger than 0" }
            isTrue(balance >= cmd.amount) { "Insufficient balance ($balance))" }
        }

        AggregateLifecycle.apply(BankAccountBalanceUpdatedEvent(
                userId = userId,
                previousBalance = balance,
                newBalance = balance - cmd.amount
        ))
    }

    @CommandHandler
    fun handle(cmd: DeleteBankAccountCommand) {
        AggregateLifecycle.apply(
                BankAccountDeletedEvent(userId = userId)
        )
    }

    @EventSourcingHandler
    fun on(e: BankAccountCreatedEvent) {
        userId = e.userId
    }

    @EventSourcingHandler
    fun on(e: BankAccountBalanceUpdatedEvent) {
        balance = e.newBalance
    }

    @EventSourcingHandler
    fun on(e: BankAccountDeletedEvent) {
        AggregateLifecycle.markDeleted()
    }
}
