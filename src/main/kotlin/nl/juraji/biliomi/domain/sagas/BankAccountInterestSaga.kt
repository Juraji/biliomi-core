package nl.juraji.biliomi.domain.sagas

import nl.juraji.biliomi.configuration.ProcessingGroups
import nl.juraji.biliomi.configuration.aggregate.BankAccountAggregateConfiguration
import nl.juraji.biliomi.domain.bankaccount.commands.AddBankAccountBalanceCommand
import nl.juraji.biliomi.domain.bankaccount.events.BankAccountDeletedEvent
import nl.juraji.biliomi.domain.bankaccount.events.InterestEndedEvent
import nl.juraji.biliomi.domain.bankaccount.events.InterestStartedEvent
import nl.juraji.biliomi.utils.LoggerCompanion
import nl.juraji.biliomi.utils.SagaAssociations
import org.axonframework.config.ProcessingGroup
import org.axonframework.deadline.DeadlineManager
import org.axonframework.deadline.annotation.DeadlineHandler
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.serialization.Revision
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

@Saga
@Revision("1.0")
@ProcessingGroup(ProcessingGroups.SAGA)
class BankAccountInterestSaga {

    @Transient
    @Autowired
    private lateinit var commandGateway: ReactorCommandGateway

    @Transient
    @Autowired
    private lateinit var deadlineManager: DeadlineManager

    @Transient
    @Autowired
    private lateinit var configuration: BankAccountAggregateConfiguration

    @StartSaga
    @SagaEventHandler(associationProperty = ASSOC_ACCOUNT)
    fun on(e: InterestStartedEvent) {
        deadlineManager.schedule(
            configuration.interestRateDuration,
            INTEREST_DEADLINE
        )
    }

    @EndSaga
    @SagaEventHandler(associationProperty = ASSOC_ACCOUNT)
    fun on(e: InterestEndedEvent) {
        deadlineManager.cancelAllWithinScope(INTEREST_DEADLINE)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = ASSOC_ACCOUNT)
    fun on(e: BankAccountDeletedEvent) {
        logger.info("Bank deleted for user ${e.accountId}, canceling interest")
        deadlineManager.cancelAllWithinScope(INTEREST_DEADLINE)
    }

    @DeadlineHandler(deadlineName = INTEREST_DEADLINE)
    fun onInterestDeadline() {
        val accountId = SagaAssociations.getAssociatedValueNonNull(ASSOC_ACCOUNT)

        commandGateway.send<Unit>(
            AddBankAccountBalanceCommand(
                accountId = accountId,
                amount = configuration.interestAmount,
                message = INTEREST_MESSAGE
            )
        ).block()

        deadlineManager.cancelAllWithinScope(INTEREST_DEADLINE)
        deadlineManager.schedule(
            configuration.interestRateDuration,
            INTEREST_DEADLINE
        )
    }

    companion object : LoggerCompanion(BankAccountInterestSaga::class) {
        const val ASSOC_ACCOUNT = "accountId"
        const val INTEREST_DEADLINE = "interest-deadline"
        const val INTEREST_MESSAGE = "Bank account interest"
    }
}
