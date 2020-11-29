package nl.juraji.biliomi.domain.bank

import nl.juraji.biliomi.domain.bank.commands.AddBalanceCommand
import nl.juraji.biliomi.domain.bank.commands.CreateBankCommand
import nl.juraji.biliomi.domain.bank.commands.TakeBalanceCommand
import nl.juraji.biliomi.domain.bank.events.BalanceUpdatedEvent
import nl.juraji.biliomi.domain.bank.events.BankCreatedEvent
import nl.juraji.biliomi.utils.extensions.uuid
import nl.juraji.biliomi.utils.validation.ValidationException
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class BankAggregateTest {
    private lateinit var fixture: AggregateTestFixture<BankAggregate>
    private val userId = uuid()

    @BeforeEach
    fun setUp() {
        fixture = AggregateTestFixture(BankAggregate::class.java)
    }

    @Test
    internal fun `should be able to create account`() {
        fixture
                .`when`(CreateBankCommand(userId))
                .expectEvents(BankCreatedEvent(userId))
    }

    @Test
    internal fun `should be able to add points`() {
        fixture
                .given(
                        BankCreatedEvent(userId),
                        BalanceUpdatedEvent(userId, 0, 25)
                )
                .`when`(AddBalanceCommand(userId, 10))
                .expectEvents(BalanceUpdatedEvent(userId, 25, 35))
    }

    @Test
    internal fun `should fail to add negative points`() {
        fixture
                .given(BankCreatedEvent(userId))
                .`when`(AddBalanceCommand(userId, -10))
                .expectException(ValidationException::class.java)
    }

    @Test
    internal fun `should be able to take points`() {
        fixture
                .given(
                        BankCreatedEvent(userId),
                        BalanceUpdatedEvent(userId, 0, 25)
                )
                .`when`(TakeBalanceCommand(userId, 10))
                .expectEvents(BalanceUpdatedEvent(userId, 25, 15))
    }

    @Test
    internal fun `should fail to take negative points`() {
        fixture
                .given(BankCreatedEvent(userId))
                .`when`(TakeBalanceCommand(userId, -10))
                .expectException(ValidationException::class.java)
    }

    @Test
    internal fun `should fail to take points when balance too low`() {
        fixture
                .given(
                        BankCreatedEvent(userId),
                        BalanceUpdatedEvent(userId, 0, 5)
                )
                .`when`(TakeBalanceCommand(userId, 10))
                .expectException(ValidationException::class.java)
    }
}
