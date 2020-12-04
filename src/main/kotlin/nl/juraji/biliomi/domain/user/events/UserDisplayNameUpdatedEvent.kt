package nl.juraji.biliomi.domain.user.events

data class UserDisplayNameUpdatedEvent(
    override val username: String,
    val displayName: String,
) : UserEvent
