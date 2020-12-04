package nl.juraji.biliomi.domain.user.events

data class AuthorityGroupCreatedEvent(
    override val groupId: String,
    val groupName: String,
    val protected: Boolean,
    val authorities: Set<String>
): AuthorityGroupEvent
