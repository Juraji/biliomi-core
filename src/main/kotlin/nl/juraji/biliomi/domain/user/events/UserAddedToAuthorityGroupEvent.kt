package nl.juraji.biliomi.domain.user.events

data class UserAddedToAuthorityGroupEvent(
    override val username: String,
    val groupId: String
) : UserEvent