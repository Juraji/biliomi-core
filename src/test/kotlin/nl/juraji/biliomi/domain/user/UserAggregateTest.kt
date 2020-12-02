package nl.juraji.biliomi.domain.user

import nl.juraji.biliomi.domain.user.commands.CreateUserCommand
import nl.juraji.biliomi.domain.user.commands.DeleteUserCommand
import nl.juraji.biliomi.domain.user.events.UserCreatedEvent
import nl.juraji.biliomi.domain.user.events.UserDeletedEvent
import nl.juraji.biliomi.utils.extensions.uuid
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class UserAggregateTest {
    private lateinit var fixture: AggregateTestFixture<UserAggregate>
    private val userId = uuid()

    @BeforeEach
    fun setUp() {
        fixture = AggregateTestFixture(UserAggregate::class.java)
    }

    @Test
    internal fun `should be able to create user`() {
        fixture
            .`when`(CreateUserCommand(userId))
            .expectEvents(UserCreatedEvent(userId))
    }

    @Test
    internal fun `should be able to delete user`() {
        fixture
            .given(UserCreatedEvent(userId))
            .`when`(DeleteUserCommand(userId))
            .expectEvents(UserDeletedEvent(userId))
            .expectMarkedDeleted()
    }
}
