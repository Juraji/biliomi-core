package nl.juraji.biliomi.domain.user.events

data class UserPasswordUpdatedEvent(
    override val username: String,
    val passwordHash: String,
) : UserEvent
