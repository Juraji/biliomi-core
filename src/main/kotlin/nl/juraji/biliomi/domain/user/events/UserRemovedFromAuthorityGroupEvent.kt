package nl.juraji.biliomi.domain.user.events

data class UserRemovedFromAuthorityGroupEvent(
    override val userId: String,
    val groupId: String
) : UserEvent
