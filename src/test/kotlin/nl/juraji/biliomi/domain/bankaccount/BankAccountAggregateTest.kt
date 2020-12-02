package nl.juraji.biliomi.domain.bankaccount

import nl.juraji.biliomi.domain.bankaccount.commands.AddBankAccountBalanceCommand
import nl.juraji.biliomi.domain.bankaccount.commands.CreateBankAccountCommand
import nl.juraji.biliomi.domain.bankaccount.commands.DeleteBankAccountCommand
import nl.juraji.biliomi.domain.bankaccount.commands.TakeBankAccountBalanceCommand
import nl.juraji.biliomi.domain.bankaccount.events.BankAccountBalanceUpdatedEvent
import nl.juraji.biliomi.domain.bankaccount.events.BankAccountCreatedEvent
import nl.juraji.biliomi.domain.bankaccount.events.BankAccountDeletedEvent
import nl.juraji.biliomi.utils.extensions.uuid
import nl.juraji.reactor.validations.ValidationException
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class BankAccountAggregateTest {
    private lateinit var fixture: AggregateTestFixture<BankAccountAggregate>
    private val userId = uuid()

    @BeforeEach
    fun setUp() {
        fixture = AggregateTestFixture(BankAccountAggregate::class.java)
    }

    @Test
    internal fun `should be able to create account`() {
        fixture
                .`when`(CreateBankAccountCommand(userId))
                .expectEvents(BankAccountCreatedEvent(userId))
    }

    @Test
    internal fun `should be able to add points`() {
        fixture
                .given(
                        BankAccountCreatedEvent(userId),
                        BankAccountBalanceUpdatedEvent(userId, 0, 25)
                )
                .`when`(AddBankAccountBalanceCommand(userId, 10))
                .expectEvents(BankAccountBalanceUpdatedEvent(userId, 25, 35))
    }

    @Test
    internal fun `should fail to add negative points`() {
        fixture
                .given(BankAccountCreatedEvent(userId))
                .`when`(AddBankAccountBalanceCommand(userId, -10))
                .expectException(ValidationException::class.java)
    }

    @Test
    internal fun `should be able to take points`() {
        fixture
                .given(
                        BankAccountCreatedEvent(userId),
                        BankAccountBalanceUpdatedEvent(userId, 0, 25)
                )
                .`when`(TakeBankAccountBalanceCommand(userId, 10))
                .expectEvents(BankAccountBalanceUpdatedEvent(userId, 25, 15))
    }

    @Test
    internal fun `should fail to take negative points`() {
        fixture
                .given(BankAccountCreatedEvent(userId))
                .`when`(TakeBankAccountBalanceCommand(userId, -10))
                .expectException(ValidationException::class.java)
    }

    @Test
    internal fun `should fail to take points when balance too low`() {
        fixture
                .given(
                        BankAccountCreatedEvent(userId),
                        BankAccountBalanceUpdatedEvent(userId, 0, 5)
                )
                .`when`(TakeBankAccountBalanceCommand(userId, 10))
                .expectException(ValidationException::class.java)
    }

    @Test
    internal fun `should be able to delete bank`() {
        fixture
                .given(
                        BankAccountCreatedEvent(userId),
                        BankAccountBalanceUpdatedEvent(userId, 0, 5)
                )
                .`when`(DeleteBankAccountCommand(userId))
                .expectEvents(BankAccountDeletedEvent(userId))
                .expectMarkedDeleted()
    }
}
