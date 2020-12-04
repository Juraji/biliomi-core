package nl.juraji.biliomi.domain.user.events

data class UserUsernameUpdatedEvent(
    override val userId: String,
    val username: String,
) : UserEvent
