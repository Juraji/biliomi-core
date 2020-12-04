package nl.juraji.biliomi.domain.user.events

data class UserAddedToAuthorityGroupEvent(
    override val userId: String,
    val groupId: String
) : UserEvent
