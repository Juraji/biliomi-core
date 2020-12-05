package nl.juraji.biliomi.domain.user.events

import org.axonframework.serialization.Revision

@Revision("1.0")
data class UserPasswordUpdatedEvent(
    override val username: String,
    val passwordHash: String,
) : UserEvent
