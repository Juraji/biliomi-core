package nl.juraji.biliomi.projections.handlers

import nl.juraji.biliomi.configuration.ProcessingGroups
import nl.juraji.biliomi.domain.bank.events.BalanceUpdatedEvent
import nl.juraji.biliomi.domain.bank.events.BankCreatedEvent
import nl.juraji.biliomi.domain.bank.events.BankDeletedEvent
import nl.juraji.biliomi.projections.BankProjection
import nl.juraji.biliomi.projections.repositories.BankProjectionRepository
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Service

@Service
@ProcessingGroup(ProcessingGroups.PROJECTIONS)
class BankProjectionEventHandler(
        private val bankProjectionRepository: BankProjectionRepository
) {

    @EventHandler
    fun on(e: BankCreatedEvent) {
        bankProjectionRepository
                .save(BankProjection(userId = e.userId))
                .block()
    }

    @EventHandler
    fun on(e: BalanceUpdatedEvent) {
        bankProjectionRepository
                .update(e.userId) { copy(balance = e.newBalance) }
                .block()
    }

    @EventHandler
    fun on(e: BankDeletedEvent) {
        bankProjectionRepository
                .deleteById(e.userId)
                .block()
    }
}
