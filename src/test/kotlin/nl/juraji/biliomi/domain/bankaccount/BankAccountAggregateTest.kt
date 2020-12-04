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
    private val accountId = uuid()
    private val userId = uuid()

    @BeforeEach
    fun setUp() {
        fixture = AggregateTestFixture(BankAccountAggregate::class.java)
    }

    @Test
    internal fun `should be able to create account`() {
        fixture
            .`when`(CreateBankAccountCommand(accountId, userId))
            .expectEvents(BankAccountCreatedEvent(accountId, userId))
    }

    @Test
    internal fun `should be able to add points`() {
        fixture
            .given(
                BankAccountCreatedEvent(accountId, userId),
                BankAccountBalanceUpdatedEvent(accountId, 0, 25)
            )
            .`when`(AddBankAccountBalanceCommand(accountId, 10, "A Message"))
            .expectEvents(BankAccountBalanceUpdatedEvent(accountId, 25, 35, "A Message"))
    }

    @Test
    internal fun `should fail to add negative points`() {
        fixture
            .given(BankAccountCreatedEvent(accountId, userId))
            .`when`(AddBankAccountBalanceCommand(accountId, -10))
            .expectException(ValidationException::class.java)
    }

    @Test
    internal fun `should be able to take points`() {
        fixture
            .given(
                BankAccountCreatedEvent(accountId, userId),
                BankAccountBalanceUpdatedEvent(accountId, 0, 25)
            )
            .`when`(TakeBankAccountBalanceCommand(accountId, 10, "A message"))
            .expectEvents(BankAccountBalanceUpdatedEvent(accountId, 25, 15, "A message"))
    }

    @Test
    internal fun `should fail to take negative points`() {
        fixture
            .given(BankAccountCreatedEvent(accountId, userId))
            .`when`(TakeBankAccountBalanceCommand(accountId, -10))
            .expectException(ValidationException::class.java)
    }

    @Test
    internal fun `should fail to take points when balance too low`() {
        fixture
            .given(
                BankAccountCreatedEvent(accountId, userId),
                BankAccountBalanceUpdatedEvent(accountId, 0, 5)
            )
            .`when`(TakeBankAccountBalanceCommand(accountId, 10))
            .expectException(ValidationException::class.java)
    }

    @Test
    internal fun `should be able to delete bank`() {
        fixture
            .given(
                BankAccountCreatedEvent(accountId, userId),
                BankAccountBalanceUpdatedEvent(accountId, 0, 5)
            )
            .`when`(DeleteBankAccountCommand(accountId))
            .expectEvents(BankAccountDeletedEvent(accountId))
            .expectMarkedDeleted()
    }
}
