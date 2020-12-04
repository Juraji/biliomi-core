package nl.juraji.biliomi.domain.user

import nl.juraji.biliomi.domain.user.commands.CreateUserCommand
import nl.juraji.biliomi.domain.user.commands.DeleteUserCommand
import nl.juraji.biliomi.domain.user.events.UserCreatedEvent
import nl.juraji.biliomi.domain.user.events.UserDeletedEvent
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class UserAggregateTest {
    private lateinit var fixture: AggregateTestFixture<UserAggregate>
    private val username = "mock-user"
    private val displayName = "Mock user"

    @BeforeEach
    fun setUp() {
        fixture = AggregateTestFixture(UserAggregate::class.java)
    }

    @Test
    internal fun `should be able to create user`() {
        fixture
            .`when`(CreateUserCommand(username, displayName))
            .expectEvents(UserCreatedEvent(username, displayName))
    }

    @Test
    internal fun `should be able to delete user`() {
        fixture
            .given(UserCreatedEvent(username, displayName))
            .`when`(DeleteUserCommand(username))
            .expectEvents(UserDeletedEvent(username))
            .expectMarkedDeleted()
    }
}
