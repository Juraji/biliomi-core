package nl.juraji.biliomi.domain.sagas

import nl.juraji.biliomi.configuration.ProcessingGroups
import nl.juraji.biliomi.domain.bankaccount.commands.CreateBankAccountCommand
import nl.juraji.biliomi.domain.bankaccount.commands.DeleteBankAccountCommand
import nl.juraji.biliomi.domain.bankaccount.events.BankAccountCreatedEvent
import nl.juraji.biliomi.domain.bankaccount.events.BankAccountDeletedEvent
import nl.juraji.biliomi.domain.user.events.UserCreatedEvent
import nl.juraji.biliomi.domain.user.events.UserDeletedEvent
import nl.juraji.biliomi.utils.LoggerCompanion
import nl.juraji.biliomi.utils.SagaAssociations
import nl.juraji.biliomi.utils.extensions.uuidV3
import org.axonframework.config.ProcessingGroup
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.serialization.Revision
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired


@Saga
@Revision("1.0")
@ProcessingGroup(ProcessingGroups.SAGA)
class UserBankAccountManagementSaga {

    @Transient
    @Autowired
    private lateinit var commandGateway: ReactorCommandGateway

    @StartSaga
    @SagaEventHandler(associationProperty = ASSOC_USER)
    fun on(e: UserCreatedEvent) {
        val accountId = uuidV3(UUID_ACCOUNTS_NS, e.username)
        val cmd = CreateBankAccountCommand(accountId = accountId, username = e.username)

        SagaLifecycle.associateWith(ASSOC_ACCOUNT, accountId)
        commandGateway.send<Unit>(cmd).block()
    }

    @SagaEventHandler(associationProperty = ASSOC_ACCOUNT)
    fun on(e: BankAccountCreatedEvent) {
        logger.info("Bank account created for user ${e.username}")
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
    }

    companion object : LoggerCompanion(UserBankAccountManagementSaga::class) {
        const val UUID_ACCOUNTS_NS = "bank-account"
        const val ASSOC_ACCOUNT = "accountId"
        const val ASSOC_USER = "username"
    }
}
