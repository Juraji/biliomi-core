package nl.juraji.biliomi.domain.user

import nl.juraji.biliomi.domain.user.commands.*
import nl.juraji.biliomi.domain.user.events.*
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class UserAggregateTest {
    private lateinit var fixture: AggregateTestFixture<UserAggregate>
    private val username = "mock-user"
    private val displayName = "Mock user"

    // AddUserToAuthorityGroupCommand
    // RemoveUserFromAuthorityGroupCommand
    // SetUserDisplayNameCommand
    // SetUserPasswordCommand

    @BeforeEach
    fun setUp() {
        fixture = AggregateTestFixture(UserAggregate::class.java)
    }

    @Test
    internal fun `should be able to create user`() {
        fixture
            .`when`(CreateUserCommand(username, displayName, "some_password_hash"))
            .expectEvents(UserCreatedEvent(username, displayName, "some_password_hash"))
    }

    @Test
    internal fun `should validate user creation`() {
        // Blank username
        fixture
            .`when`(CreateUserCommand("", displayName))
            .expectExceptionMessage("Username may not be empty")

        // Blank display name
        fixture
            .`when`(CreateUserCommand(username, ""))
            .expectExceptionMessage("Display name may not be empty")

        // Blank password (non-null)
        fixture
            .`when`(CreateUserCommand(username, displayName, ""))
            .expectExceptionMessage("Password hash may not be empty")
    }

    @Test
    internal fun `should add user to authority group`() {
        fixture
            .given(UserCreatedEvent(username, displayName))
            .`when`(AddUserToAuthorityGroupCommand(username, "group#1"))
            .expectEvents(UserAddedToAuthorityGroupEvent(username, "group#1"))
    }

    @Test
    internal fun `should validate group addition`() {
        // Blank group id
        fixture
            .given(UserCreatedEvent(username, displayName))
            .`when`(AddUserToAuthorityGroupCommand(username, ""))
            .expectExceptionMessage("Group id may not be empty")
    }

    @Test
    internal fun `should prevent adding to same group twice`() {
        fixture
            .given(
                UserCreatedEvent(username, displayName),
                UserAddedToAuthorityGroupEvent(username, "group#1")
            )
            .`when`(AddUserToAuthorityGroupCommand(username, "group#1"))
            .expectExceptionMessage("User $username is already in group with id group#1")
    }

    @Test
    internal fun `should remove user from group`() {
        fixture
            .given(
                UserCreatedEvent(username, displayName),
                UserAddedToAuthorityGroupEvent(username, "group#1")
            )
            .`when`(RemoveUserFromAuthorityGroupCommand(username, "group#1"))
            .expectEvents(UserRemovedFromAuthorityGroupEvent(username, "group#1"))
    }

    @Test
    internal fun `should validate group removal`() {
        // Blank group id
        fixture
            .given(
                UserCreatedEvent(username, displayName),
                UserAddedToAuthorityGroupEvent(username, "group#1")
            )
            .`when`(RemoveUserFromAuthorityGroupCommand(username, ""))
            .expectExceptionMessage("Group id may not be empty")
    }

    @Test
    internal fun `should prevent removal from group which user not is in`() {
        fixture
            .given(
                UserCreatedEvent(username, displayName),
                UserAddedToAuthorityGroupEvent(username, "group#1")
            )
            .`when`(RemoveUserFromAuthorityGroupCommand(username, "group#1"))
            .expectExceptionMessage("User $username is not in group with id group#2")
    }

    @Test
    internal fun `should update display name`() {
        fixture
            .given(UserCreatedEvent(username, displayName))
            .`when`(SetUserDisplayNameCommand(username, "Other name"))
            .expectEvents(UserDisplayNameUpdatedEvent(username, "Other name"))
    }

    @Test
    internal fun `should validate display name updates`() {
        // Blank display name
        fixture
            .given(UserCreatedEvent(username, displayName))
            .`when`(SetUserDisplayNameCommand(username, ""))
            .expectExceptionMessage("Display name may not be empty")

        // Display name not changed
        fixture
            .given(UserCreatedEvent(username, displayName))
            .`when`(SetUserPasswordCommand(username, displayName))
            .expectExceptionMessage("Display name is the same as the current display name")
    }

    @Test
    internal fun `should update password`() {
        fixture
            .given(UserCreatedEvent(username, displayName))
            .`when`(SetUserPasswordCommand(username, "other_hash"))
            .expectEvents(UserPasswordUpdatedEvent(username, "other_hash"))
    }

    @Test
    internal fun `should validate password updates`() {
        // Blank display name
        fixture
            .given(UserCreatedEvent(username, displayName))
            .`when`(SetUserPasswordCommand(username, ""))
            .expectExceptionMessage("Password hash may not be empty")
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
