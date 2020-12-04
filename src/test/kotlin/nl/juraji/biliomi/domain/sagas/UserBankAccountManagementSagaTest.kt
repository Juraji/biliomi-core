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
    private val accountId = UserBankAccountManagementSaga.generateAccountId(userId)
    private val username = "MockUser"

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
            .whenPublishingA(UserCreatedEvent(userId, username))
            .expectDispatchedCommands(CreateBankAccountCommand(accountId, userId))
            .expectAssociationWith(UserBankAccountManagementSaga.ASSOC_USER, userId)
            .expectAssociationWith(UserBankAccountManagementSaga.ASSOC_ACCOUNT, accountId)
    }

    @Test
    internal fun `should delete bank account on User deleted event`() {
        fixture
            .givenAPublished(UserCreatedEvent(userId, username))
            .andThenAPublished(BankAccountCreatedEvent(accountId, userId))
            .whenPublishingA(UserDeletedEvent(userId))
            .expectDispatchedCommands(DeleteBankAccountCommand(accountId))
    }

    @Test
    internal fun `should end when user bank account was deleted`() {
        fixture
            .givenAPublished(UserCreatedEvent(userId, username))
            .andThenAPublished(BankAccountCreatedEvent(accountId, userId))
            .andThenAPublished(UserDeletedEvent(userId))
            .whenPublishingA(BankAccountDeletedEvent(accountId))
            .expectActiveSagas(0)
    }

    @Test
    internal fun `should schedule interest event after on InterestStartedEvent`() {
        every { configuration.interestRateDuration } returns Duration.ofMinutes(5)

        fixture.givenAPublished(UserCreatedEvent(userId, username))
            .andThenAPublished(BankAccountCreatedEvent(accountId, userId))
            .whenPublishingA(InterestStartedEvent(accountId))
            .expectScheduledDeadlineWithName(
                Duration.ofMinutes(5),
                UserBankAccountManagementSaga.INTEREST_DEADLINE
            )
    }

    @Test
    internal fun `should cancel interest schedules on InterestEndedEvent`() {
        every { configuration.interestRateDuration } returns Duration.ofMinutes(5)
        every { configuration.interestAmount } returns 10

        fixture.givenAPublished(UserCreatedEvent(userId, username))
            .andThenAPublished(BankAccountCreatedEvent(accountId, userId))
            .andThenAPublished(InterestStartedEvent(accountId))
            .andThenTimeElapses(Duration.ofMinutes(3))
            .whenPublishingA(InterestEndedEvent(accountId))
            .expectNoScheduledDeadlines()
    }

    @Test
    internal fun `should add configured interest after configured duration`() {
        every { configuration.interestRateDuration } returns Duration.ofMinutes(5)
        every { configuration.interestAmount } returns 10

        fixture.givenAPublished(UserCreatedEvent(userId, username))
            .andThenAPublished(BankAccountCreatedEvent(accountId, userId))
            .andThenAPublished(InterestStartedEvent(accountId))
            .whenTimeElapses(Duration.ofMinutes(10))
            .expectDispatchedCommands(
                AddBankAccountBalanceCommand(accountId, 10, "Interest after PT5M: 10"),
                AddBankAccountBalanceCommand(accountId, 10, "Interest after PT5M: 10")
            )
    }
}
