package nl.juraji.biliomi.domain.user

import nl.juraji.biliomi.configuration.security.Authorities
import nl.juraji.biliomi.domain.user.commands.CreateAuthorityGroupCommand
import nl.juraji.biliomi.domain.user.commands.DeleteAuthorityGroupCommand
import nl.juraji.biliomi.domain.user.commands.UpdateAuthorityGroupCommand
import nl.juraji.biliomi.domain.user.events.AuthorityGroupCreatedEvent
import nl.juraji.biliomi.domain.user.events.AuthorityGroupDeletedEvent
import nl.juraji.biliomi.domain.user.events.AuthorityGroupUpdatedEvent
import nl.juraji.biliomi.utils.extensions.uuid
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class AuthorityGroupAggregateTest {
    private lateinit var fixture: AggregateTestFixture<AuthorityGroupAggregate>
    private val groupId = uuid()
    private val groupName = "group#1"

    @BeforeEach
    fun setUp() {
        fixture = AggregateTestFixture(AuthorityGroupAggregate::class.java)
    }

    @Test
    internal fun `should create new authority group`() {
        val authorities = setOf(Authorities.USERS_CREATE, Authorities.USERS_READ_ALL)

        fixture
            .`when`(CreateAuthorityGroupCommand(groupId, groupName, authorities))
            .expectEvents(AuthorityGroupCreatedEvent(groupId, groupName, authorities, false))
    }

    @Test
    internal fun `should validate group creation`() {
        // Blank group id
        fixture
            .`when`(CreateAuthorityGroupCommand("", groupName, emptySet()))
            .expectExceptionMessage("Group id should not be blank")

        // Blank group name
        fixture
            .`when`(CreateAuthorityGroupCommand(groupId, "", emptySet()))
            .expectExceptionMessage("Group name should not be blank")

        // Empty authorities
        fixture
            .`when`(CreateAuthorityGroupCommand(groupId, groupName, emptySet()))
            .expectExceptionMessage("Group authorities may not be empty")

        // Blank authorities (in set)
        fixture
            .`when`(CreateAuthorityGroupCommand(groupId, groupName, setOf(Authorities.USERS_READ_ALL, "")))
            .expectExceptionMessage("Group authorities may not contain empty values")

        // Invalid authorities (in set)
        fixture
            .`when`(CreateAuthorityGroupCommand(groupId, groupName, setOf(Authorities.USERS_READ_ALL, "ROLE_UNKNOWN")))
            .expectExceptionMessage("Unknown entry found in authorities")
    }

    @Test
    internal fun `should update authority group`() {
        val authorities = setOf(Authorities.USERS_CREATE, Authorities.USERS_READ_ALL)

        fixture
            .given(AuthorityGroupCreatedEvent(groupId, groupName, authorities, false))
            .`when`(UpdateAuthorityGroupCommand(groupId, "Other name", authorities.plus(Authorities.GROUPS_DELETE)))
            .expectEvents(AuthorityGroupUpdatedEvent(groupId, "Other name", authorities.plus(Authorities.GROUPS_DELETE)))
    }

    @Test
    internal fun `should validate authority group updates`() {
        val authorities = setOf(Authorities.USERS_CREATE, Authorities.USERS_READ_ALL)

        // Blank group name
        fixture
            .given(AuthorityGroupCreatedEvent(groupId, groupName, authorities, false))
            .`when`(UpdateAuthorityGroupCommand(groupId, "", authorities))
            .expectExceptionMessage("Group name should not be blank")


        // Nothing changed
        fixture
            .given(AuthorityGroupCreatedEvent(groupId, groupName, authorities, false))
            .`when`(UpdateAuthorityGroupCommand(groupId, groupName, authorities))
            .expectExceptionMessage("No properties were updated")


        // Empty authorities
        fixture
            .given(AuthorityGroupCreatedEvent(groupId, groupName, authorities, false))
            .`when`(UpdateAuthorityGroupCommand(groupId, groupName, emptySet()))
            .expectExceptionMessage("Group authorities may not be empty")

        // Blank authorities (in set)
        fixture
            .given(AuthorityGroupCreatedEvent(groupId, groupName, authorities, false))
            .`when`(UpdateAuthorityGroupCommand(groupId, groupName, setOf(Authorities.USERS_READ_ALL, "")))
            .expectExceptionMessage("Group authorities may not contain empty values")

        // Invalid authorities (in set)
        fixture
            .given(AuthorityGroupCreatedEvent(groupId, groupName, authorities, false))
            .`when`(UpdateAuthorityGroupCommand(groupId, groupName, setOf(Authorities.USERS_READ_ALL, "ROLE_UNKNOWN")))
            .expectExceptionMessage("Unknown entry found in authorities")
    }

    @Test
    internal fun `should allow rename of protected authority groups`() {
        val authorities = setOf(Authorities.USERS_CREATE, Authorities.USERS_READ_ALL)

        fixture
            .given(AuthorityGroupCreatedEvent(groupId, groupName, authorities, true))
            .`when`(UpdateAuthorityGroupCommand(groupId, "Other name", authorities))
            .expectEvents(AuthorityGroupUpdatedEvent(groupId, "Other name", authorities))
    }

    @Test
    internal fun `should protect protected authority groups from updates to authorities`() {
        val authorities = setOf(Authorities.USERS_CREATE, Authorities.USERS_READ_ALL)

        fixture
            .given(AuthorityGroupCreatedEvent(groupId, groupName, authorities, true))
            .`when`(UpdateAuthorityGroupCommand(groupId, groupName, setOf(Authorities.USERS_READ_ALL)))
            .expectExceptionMessage("Authority group $groupName is protected and may not have its authorities updated")
    }

    @Test
    internal fun `should delete authority group`() {
        val authorities = setOf(Authorities.USERS_CREATE, Authorities.USERS_READ_ALL)

        fixture
            .given(AuthorityGroupCreatedEvent(groupId, groupName, authorities, false))
            .`when`(DeleteAuthorityGroupCommand(groupId))
            .expectEvents(AuthorityGroupDeletedEvent(groupId))
            .expectMarkedDeleted()
    }

    @Test
    internal fun `should protect protected authority group from deletion`() {
        val authorities = setOf(Authorities.USERS_CREATE, Authorities.USERS_READ_ALL)

        fixture
            .given(AuthorityGroupCreatedEvent(groupId, groupName, authorities, true))
            .`when`(DeleteAuthorityGroupCommand(groupId))
            .expectExceptionMessage("Authority group $groupName is protected and can not be deleted")
    }
}
