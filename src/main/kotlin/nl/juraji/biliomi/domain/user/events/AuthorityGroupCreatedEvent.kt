package nl.juraji.biliomi.domain.user.events

data class AuthorityGroupCreatedEvent(
    override val groupId: String,
    val groupName: String,
    val authorities: Set<String>,
    val protected: Boolean
): AuthorityGroupEvent
