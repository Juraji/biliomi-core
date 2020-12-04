package nl.juraji.biliomi.projections.handlers

import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import nl.juraji.biliomi.domain.bankaccount.events.BankAccountBalanceUpdatedEvent
import nl.juraji.biliomi.domain.bankaccount.events.BankAccountCreatedEvent
import nl.juraji.biliomi.domain.bankaccount.events.BankAccountDeletedEvent
import nl.juraji.biliomi.projections.BankProjection
import nl.juraji.biliomi.projections.repositories.BankProjectionRepository
import nl.juraji.biliomi.utils.extensions.uuid
import nl.juraji.biliomi.utils.returnsEmptyMono
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class BankProjectionEventHandlerTest {

    private val accountId = uuid()
    private val username = "mock-user"

    @MockK
    private lateinit var bankProjectionRepository: BankProjectionRepository

    @InjectMockKs
    private lateinit var bankProjectionEventHandler: BankProjectionEventHandler

    @Test
    internal fun `should persist new bank account`() {
        every { bankProjectionRepository.save(any()) }.returnsEmptyMono()

        bankProjectionEventHandler.on(BankAccountCreatedEvent(accountId, username))

        verify {
            bankProjectionRepository.save(BankProjection(accountId, username))
        }
    }

    @Test
    internal fun `should update bank account balance`() {
        val mapperSlot: CapturingSlot<BankProjection.() -> BankProjection> = slot()
        every { bankProjectionRepository.update(any(), capture(mapperSlot)) }.returnsEmptyMono()

        bankProjectionEventHandler.on(BankAccountBalanceUpdatedEvent(accountId, 0, 10))

        assertEquals(
            BankProjection(accountId = accountId, username = username, balance = 10),
            mapperSlot.captured.invoke(BankProjection(accountId, username))
        )
    }

    @Test
    internal fun `should delete bank accounts`() {
        every { bankProjectionRepository.deleteById(any()) }.returnsEmptyMono()

        bankProjectionEventHandler.on(BankAccountDeletedEvent(accountId))

        verify {
            bankProjectionRepository.deleteById(accountId)
        }
    }
}
