package nl.juraji.biliomi.domain.sagas

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
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
import nl.juraji.biliomi.utils.extensions.uuid
import nl.juraji.biliomi.utils.returnsEmptyMono
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.test.saga.SagaTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Duration

@ExtendWith(MockKExtension::class)
internal class UserBankAccountManagementSagaTest {
    private lateinit var fixture: SagaTestFixture<UserBankAccountManagementSaga>
    private val userId = uuid()

    @MockK
    private lateinit var commandGateway: ReactorCommandGateway

    @MockK
    private lateinit var configuration: BankAccountAggregateConfiguration

    @BeforeEach
    internal fun setUp() {
        fixture = SagaTestFixture(UserBankAccountManagementSaga::class.java)
        fixture.registerCommandGateway(ReactorCommandGateway::class.java, commandGateway)
        fixture.registerResource(configuration)

        every { commandGateway.send<Any>(any()) }.returnsEmptyMono()
    }

    @Test
    internal fun `should create bank account for newly created User`() {
        fixture.givenNoPriorActivity()
            .whenPublishingA(UserCreatedEvent(userId))
            .expectDispatchedCommands(CreateBankAccountCommand(userId))
            .expectAssociationWith(UserBankAccountManagementSaga.ASSOC_USER, userId)
    }

    @Test
    internal fun `should delete bank account on User deleted event`() {
        fixture
            .givenAPublished(UserCreatedEvent(userId))
            .andThenAPublished(BankAccountCreatedEvent(userId))
            .whenPublishingA(UserDeletedEvent(userId))
            .expectDispatchedCommands(DeleteBankAccountCommand(userId))
    }

    @Test
    internal fun `should end when user bank account was deleted`() {
        fixture
            .givenAPublished(UserCreatedEvent(userId))
            .andThenAPublished(BankAccountCreatedEvent(userId))
            .andThenAPublished(UserDeletedEvent(userId))
            .whenPublishingA(BankAccountDeletedEvent(userId))
            .expectActiveSagas(0)
    }

    @Test
    internal fun `should schedule interest event after on InterestStartedEvent`() {
        every { configuration.interestRateDuration } returns Duration.ofMinutes(5)

        fixture.givenAPublished(UserCreatedEvent(userId))
            .andThenAPublished(BankAccountCreatedEvent(userId))
            .whenPublishingA(InterestStartedEvent(userId))
            .expectScheduledDeadlineWithName(
                Duration.ofMinutes(5),
                UserBankAccountManagementSaga.INTEREST_DEADLINE
            )
    }

    @Test
    internal fun `should cancel interest schedules on InterestEndedEvent`() {
        every { configuration.interestRateDuration } returns Duration.ofMinutes(5)
        every { configuration.interestAmount } returns 10

        fixture.givenAPublished(UserCreatedEvent(userId))
            .andThenAPublished(BankAccountCreatedEvent(userId))
            .andThenAPublished(InterestStartedEvent(userId))
            .andThenTimeElapses(Duration.ofMinutes(3))
            .whenPublishingA(InterestEndedEvent(userId))
            .expectNoScheduledDeadlines()
    }

    @Test
    internal fun `should add configured interest after configured duration`() {
        every { configuration.interestRateDuration } returns Duration.ofMinutes(5)
        every { configuration.interestAmount } returns 10

        fixture.givenAPublished(UserCreatedEvent(userId))
            .andThenAPublished(BankAccountCreatedEvent(userId))
            .andThenAPublished(InterestStartedEvent(userId))
            .whenTimeElapses(Duration.ofMinutes(10))
            .expectDispatchedCommands(
                AddBankAccountBalanceCommand(userId, 10),
                AddBankAccountBalanceCommand(userId, 10)
            )
    }
}
