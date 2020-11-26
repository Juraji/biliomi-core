package nl.juraji.biliomi.domain.user

import nl.juraji.biliomi.domain.user.commands.AddPointsCommand
import nl.juraji.biliomi.domain.user.commands.CreateUserCommand
import nl.juraji.biliomi.domain.user.commands.SubtractPointsCommand
import nl.juraji.biliomi.domain.user.events.PointBalanceUpdatedEvent
import nl.juraji.biliomi.domain.user.events.UserCreatedEvent
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class UserAggregateTest {

    private lateinit var fixture: AggregateTestFixture<UserAggregate>

    @BeforeEach
    internal fun setUp() {
        fixture = AggregateTestFixture(UserAggregate::class.java)
    }

    @Test
    internal fun `should create user`() {
        val userId = UserId()
        val username = "TestUser"

        fixture
                .`when`(
                        CreateUserCommand(userId, username)
                )
                .expectSuccessfulHandlerExecution()
                .expectEvents(
                        UserCreatedEvent(userId, username)
                )
    }

    @Test
    internal fun `should successfully add points`() {
        val userId = UserId()

        fixture
                .given(
                        UserCreatedEvent(userId, "TestUser"),
                        PointBalanceUpdatedEvent(userId, 0, 25)
                )
                .`when`(
                        AddPointsCommand(userId, 50)
                )
                .expectSuccessfulHandlerExecution()
                .expectEvents(
                        PointBalanceUpdatedEvent(
                                userId = userId,
                                previousBalance = 25,
                                newBalance = 75
                        )
                )
    }

    @Test
    internal fun `should successfully subtract points`() {
        val userId = UserId()

        fixture
                .given(
                        UserCreatedEvent(userId, "TestUser"),
                        PointBalanceUpdatedEvent(userId, 0, 75)
                )
                .`when`(
                        SubtractPointsCommand(userId, 50)
                )
                .expectSuccessfulHandlerExecution()
                .expectEvents(
                        PointBalanceUpdatedEvent(
                                userId = userId,
                                previousBalance = 75,
                                newBalance = 25
                        )
                )
    }
}
