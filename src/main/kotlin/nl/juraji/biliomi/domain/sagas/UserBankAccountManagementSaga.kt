package nl.juraji.biliomi.domain.sagas

import nl.juraji.biliomi.configuration.ProcessingGroups
import nl.juraji.biliomi.configuration.aggregate.BankAccountAggregateConfiguration
import nl.juraji.biliomi.domain.bankaccount.commands.AddBankAccountBalanceCommand
import nl.juraji.biliomi.domain.bankaccount.commands.CreateBankAccountCommand
import nl.juraji.biliomi.domain.bankaccount.commands.DeleteBankAccountCommand
import nl.juraji.biliomi.domain.bankaccount.events.BankAccountCreatedEvent
import nl.juraji.biliomi.domain.bankaccount.events.BankAccountDeletedEvent
import nl.juraji.biliomi.domain.bankaccount.events.InterestEndedEvent
import nl.juraji.biliomi.domain.bankaccount.events.InterestStartedEvent
import nl.juraji.biliomi.domain.user.events.UserCreatedEvent
import nl.juraji.biliomi.domain.user.events.UserDeletedEvent
import nl.juraji.biliomi.utils.LoggerCompanion
import nl.juraji.biliomi.utils.SagaAssociations
import nl.juraji.biliomi.utils.extensions.uuid
import org.axonframework.config.ProcessingGroup
import org.axonframework.deadline.DeadlineManager
import org.axonframework.deadline.annotation.DeadlineHandler
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.serialization.Revision
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration


@Saga
@Revision("1.0")
@ProcessingGroup(ProcessingGroups.SAGA)
class UserBankAccountManagementSaga {

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
    @SagaEventHandler(associationProperty = ASSOC_USER)
    fun on(e: UserCreatedEvent) {
        logger.info("Creating bank for user ${e.username}")
        val accountId = generateAccountId(e.username)

        SagaLifecycle.associateWith(ASSOC_ACCOUNT, accountId)
        commandGateway.send<Unit>(
            CreateBankAccountCommand(
                accountId = accountId,
                username = e.username
            )
        ).block()
    }

    @SagaEventHandler(associationProperty = ASSOC_ACCOUNT)
    fun on(e: BankAccountCreatedEvent) {
        logger.info("Bank created for user ${e.username}")
    }

    @SagaEventHandler(associationProperty = ASSOC_ACCOUNT)
    fun on(e: InterestStartedEvent) {
        deadlineManager.schedule(
            configuration.interestRateDuration,
            INTEREST_DEADLINE
        )
    }

    @SagaEventHandler(associationProperty = ASSOC_ACCOUNT)
    fun on(e: InterestEndedEvent) {
        deadlineManager.cancelAllWithinScope(INTEREST_DEADLINE)
    }

    @DeadlineHandler(deadlineName = INTEREST_DEADLINE)
    fun onInterestDeadline() {
        val accountId = SagaAssociations.getAssociatedValueNonNull(ASSOC_ACCOUNT)

        commandGateway.send<Unit>(
            AddBankAccountBalanceCommand(
                accountId = accountId,
                amount = configuration.interestAmount,
                message = "Bank account interest"
            )
        ).block()

        deadlineManager.cancelAllWithinScope(INTEREST_DEADLINE)
        deadlineManager.schedule(
            configuration.interestRateDuration,
            INTEREST_DEADLINE
        )
    }

    @SagaEventHandler(associationProperty = ASSOC_USER)
    fun on(e: UserDeletedEvent) {
        logger.info("Deleting bank for user ${e.username}")
        val accountId = SagaAssociations.getAssociatedValueNonNull(ASSOC_ACCOUNT)
        commandGateway.send<Unit>(DeleteBankAccountCommand(accountId = accountId)).block()
    }

    @EndSaga
    @SagaEventHandler(associationProperty = ASSOC_ACCOUNT)
    fun on(e: BankAccountDeletedEvent) {
        logger.info("Bank deleted for user ${e.accountId}")
        deadlineManager.cancelAllWithinScope(INTEREST_DEADLINE)
    }

    companion object : LoggerCompanion(UserBankAccountManagementSaga::class) {
        private const val ACCOUNT_ID_SUFFIX = "bank-account"
        const val ASSOC_ACCOUNT = "accountId"
        const val ASSOC_USER = "username"
        const val INTEREST_DEADLINE = "interest-deadline"

        fun generateAccountId(username: String) = uuid(username, ACCOUNT_ID_SUFFIX)
    }
}
