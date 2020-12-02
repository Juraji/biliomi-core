package nl.juraji.biliomi.domain.sagas

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import nl.juraji.biliomi.domain.bankaccount.commands.CreateBankAccountCommand
import nl.juraji.biliomi.domain.bankaccount.commands.DeleteBankAccountCommand
import nl.juraji.biliomi.domain.bankaccount.events.BankAccountCreatedEvent
import nl.juraji.biliomi.domain.bankaccount.events.BankAccountDeletedEvent
import nl.juraji.biliomi.domain.user.events.UserCreatedEvent
import nl.juraji.biliomi.domain.user.events.UserDeletedEvent
import nl.juraji.biliomi.utils.extensions.uuid
import nl.juraji.biliomi.utils.returnsEmptyMono
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.test.saga.SagaTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class UserBankAccountSagaTest {
    private lateinit var fixture: SagaTestFixture<UserBankAccountSaga>
    private val userId = uuid()

    @MockK
    private lateinit var commandGateway: ReactorCommandGateway

    @BeforeEach
    internal fun setUp() {
        fixture = SagaTestFixture(UserBankAccountSaga::class.java)
        fixture.registerCommandGateway(ReactorCommandGateway::class.java, commandGateway)

        every { commandGateway.send<Any>(any()) }.returnsEmptyMono()
    }

    @Test
    internal fun `should create bank account for newly created User`() {
        fixture.givenNoPriorActivity()
            .whenPublishingA(UserCreatedEvent(userId))
            .expectDispatchedCommands(CreateBankAccountCommand(userId))
            .expectAssociationWith("userId", userId)
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
}
