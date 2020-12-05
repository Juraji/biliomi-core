package nl.juraji.biliomi.domain.sagas

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import nl.juraji.biliomi.configuration.aggregate.BankAccountAggregateConfiguration
import nl.juraji.biliomi.domain.bankaccount.commands.AddBankAccountBalanceCommand
import nl.juraji.biliomi.domain.bankaccount.events.InterestEndedEvent
import nl.juraji.biliomi.domain.bankaccount.events.InterestStartedEvent
import nl.juraji.biliomi.utils.extensions.uuid
import nl.juraji.biliomi.utils.returnsEmptyMono
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.test.saga.SagaTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Duration

@ExtendWith(MockKExtension::class)
internal class BankAccountInterestSagaTest {
    private lateinit var fixture: SagaTestFixture<BankAccountInterestSaga>
    private val accountId = uuid()

    @MockK
    private lateinit var commandGateway: ReactorCommandGateway

    @MockK
    private lateinit var configuration: BankAccountAggregateConfiguration

    @BeforeEach
    internal fun setUp() {
        fixture = SagaTestFixture(BankAccountInterestSaga::class.java)
        fixture.registerCommandGateway(ReactorCommandGateway::class.java, commandGateway)
        fixture.registerResource(configuration)

        every { commandGateway.send<Any>(any()) }.returnsEmptyMono()
        every { configuration.interestRateDuration } returns Duration.ofMinutes(5)
        every { configuration.interestAmount } returns 10
    }

    @Test
    internal fun `should schedule interest event on InterestStartedEvent`() {
        fixture
            .whenPublishingA(InterestStartedEvent(accountId))
            .expectScheduledDeadlineWithName(
                Duration.ofMinutes(5),
                BankAccountInterestSaga.INTEREST_DEADLINE
            )
            .expectAssociationWith(BankAccountInterestSaga.ASSOC_ACCOUNT, accountId)
    }

    @Test
    internal fun `should add configured interest after configured duration`() {
        fixture
            .givenAPublished(InterestStartedEvent(accountId))
            .whenTimeElapses(Duration.ofMinutes(10))
            .expectDispatchedCommands(
                AddBankAccountBalanceCommand(accountId, 10, BankAccountInterestSaga.INTEREST_MESSAGE),
                AddBankAccountBalanceCommand(accountId, 10, BankAccountInterestSaga.INTEREST_MESSAGE)
            )
    }

    @Test
    internal fun `should cancel interest schedules on InterestEndedEvent`() {
        fixture
            .givenAPublished(InterestStartedEvent(accountId))
            .andThenTimeElapses(Duration.ofMinutes(3))
            .whenPublishingA(InterestEndedEvent(accountId))
            .expectNoScheduledDeadlines()
    }
}
