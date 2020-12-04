package nl.juraji.biliomi.domain.user.events

data class UserPasswordUpdatedEvent(
    override val userId: String,
    val passwordHash: String,
) : UserEvent
