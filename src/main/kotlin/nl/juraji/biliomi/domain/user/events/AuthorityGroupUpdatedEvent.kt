package nl.juraji.biliomi.domain.user.events

data class AuthorityGroupUpdatedEvent(
    override val groupId: String,
    val groupName: String,
    val authorities: Set<String>
) : AuthorityGroupEvent
