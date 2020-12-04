package nl.juraji.biliomi.domain.user.events

data class UserRemovedFromAuthorityGroupEvent(
    override val username: String,
    val groupId: String
) : UserEvent
