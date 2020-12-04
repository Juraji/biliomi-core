package nl.juraji.biliomi.domain.bankaccount

import nl.juraji.biliomi.domain.bankaccount.commands.*
import nl.juraji.biliomi.domain.bankaccount.events.*
import nl.juraji.reactor.validations.validate
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class BankAccountAggregate() {

    @AggregateIdentifier
    private lateinit var accountId: String
    private lateinit var username: String
    private var balance: Long = 0
    private var interestStarted: Boolean = false

    @CommandHandler
    constructor(cmd: CreateBankAccountCommand) : this() {
        AggregateLifecycle.apply(
            BankAccountCreatedEvent(
                accountId = cmd.accountId,
                username = cmd.username
            )
        )
    }

    @CommandHandler
    fun handle(cmd: AddBankAccountBalanceCommand) {
        validate {
            isTrue(cmd.amount > 0) { "Amount must be larger than 0" }
        }

        AggregateLifecycle.apply(
            BankAccountBalanceUpdatedEvent(
                accountId = accountId,
                previousBalance = balance,
                newBalance = balance + cmd.amount,
                message = cmd.message
            )
        )
    }

    @CommandHandler
    fun handle(cmd: TakeBankAccountBalanceCommand) {
        validate {
            isTrue(cmd.amount > 0) { "Amount must be larger than 0" }
            isTrue(balance >= cmd.amount) { "Insufficient balance ($balance))" }
        }

        AggregateLifecycle.apply(
            BankAccountBalanceUpdatedEvent(
                accountId = accountId,
                previousBalance = balance,
                newBalance = balance - cmd.amount,
                message = cmd.message
            )
        )
    }

    @CommandHandler
    fun handle(cmd: StartInterestCommand) {
        validate {
            isFalse(interestStarted) { "Interest has already been started for $username" }
        }

        AggregateLifecycle.apply(InterestStartedEvent(accountId))
    }

    @CommandHandler
    fun handle(cmd: EndInterestCommand) {
        validate {
            isTrue(interestStarted) { "Interest has not yey been started for $username" }
        }

        AggregateLifecycle.apply(InterestEndedEvent(accountId))
    }

    @CommandHandler
    fun handle(cmd: DeleteBankAccountCommand) {
        AggregateLifecycle.apply(
            BankAccountDeletedEvent(accountId = accountId)
        )
    }

    @EventSourcingHandler
    fun on(e: BankAccountCreatedEvent) {
        accountId = e.accountId
        username = e.username
    }

    @EventSourcingHandler
    fun on(e: BankAccountBalanceUpdatedEvent) {
        balance = e.newBalance
    }

    @EventSourcingHandler
    fun on(e: BankAccountDeletedEvent) {
        AggregateLifecycle.markDeleted()
    }

    @EventSourcingHandler
    fun on(e: InterestStartedEvent) {
        interestStarted = true
    }

    @EventSourcingHandler
    fun on(e: InterestEndedEvent) {
        interestStarted = false
    }
}
