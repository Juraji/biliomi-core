package nl.juraji.biliomi.domain.sagas

import nl.juraji.biliomi.configuration.ProcessingGroups
import nl.juraji.biliomi.domain.bankaccount.commands.CreateBankAccountCommand
import nl.juraji.biliomi.domain.bankaccount.commands.DeleteBankAccountCommand
import nl.juraji.biliomi.domain.bankaccount.events.BankAccountCreatedEvent
import nl.juraji.biliomi.domain.bankaccount.events.BankAccountDeletedEvent
import nl.juraji.biliomi.domain.user.events.UserCreatedEvent
import nl.juraji.biliomi.domain.user.events.UserDeletedEvent
import nl.juraji.biliomi.utils.LoggerCompanion
import org.axonframework.config.ProcessingGroup
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
class UserBankAccountSaga {

    @Transient
    @Autowired
    private lateinit var commandGateway: ReactorCommandGateway

    @StartSaga
    @SagaEventHandler(associationProperty = "userId")
    fun on(e: UserCreatedEvent) {
        logger.info("Creating bank for user ${e.userId}")
        commandGateway.send<Unit>(CreateBankAccountCommand(userId = e.userId)).block()
    }

    @SagaEventHandler(associationProperty = "userId")
    fun on(e: UserDeletedEvent) {
        logger.info("Deleting bank for user ${e.userId}")
        commandGateway.send<Unit>(DeleteBankAccountCommand(userId = e.userId)).block()
    }

    @SagaEventHandler(associationProperty = "userId")
    fun on(e: BankAccountCreatedEvent) {
        logger.info("Bank created for user ${e.userId}")
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "userId")
    fun on(e: BankAccountDeletedEvent) {
        logger.info("Bank deleted for user ${e.userId}")
    }

    companion object : LoggerCompanion(UserBankAccountSaga::class)
}
