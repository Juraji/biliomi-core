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
internal class UserBankAccountManagementSagaTest {
    private lateinit var fixture: SagaTestFixture<UserBankAccountManagementSaga>
    private val username = "mock-user"
    private val accountId = uuid(username, UserBankAccountManagementSaga.ACCOUNT_ID_SUFFIX)

    @MockK
    private lateinit var commandGateway: ReactorCommandGateway

    @BeforeEach
    internal fun setUp() {
        fixture = SagaTestFixture(UserBankAccountManagementSaga::class.java)
        fixture.registerCommandGateway(ReactorCommandGateway::class.java, commandGateway)

        every { commandGateway.send<Any>(any()) }.returnsEmptyMono()
    }

    @Test
    internal fun `should create bank account for newly created User`() {
        fixture.givenNoPriorActivity()
            .whenPublishingA(UserCreatedEvent(username, username))
            .expectDispatchedCommands(CreateBankAccountCommand(accountId, username))
            .expectAssociationWith(UserBankAccountManagementSaga.ASSOC_USER, username)
            .expectAssociationWith(UserBankAccountManagementSaga.ASSOC_ACCOUNT, accountId)
    }

    @Test
    internal fun `should delete bank account on User deleted event`() {
        fixture
            .givenAPublished(UserCreatedEvent(username, username))
            .andThenAPublished(BankAccountCreatedEvent(accountId, username))
            .whenPublishingA(UserDeletedEvent(username))
            .expectDispatchedCommands(DeleteBankAccountCommand(accountId))
    }

    @Test
    internal fun `should end when user bank account was deleted`() {
        fixture
            .givenAPublished(UserCreatedEvent(username, username))
            .andThenAPublished(BankAccountCreatedEvent(accountId, username))
            .andThenAPublished(UserDeletedEvent(username))
            .whenPublishingA(BankAccountDeletedEvent(accountId))
            .expectActiveSagas(0)
    }
}
