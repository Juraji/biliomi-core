package nl.juraji.biliomi.domain.user.events

import org.axonframework.serialization.Revision

@Revision("1.0")
data class UserCreatedEvent(
    override val userId: String,
    val username: String,
    val passwordHash: String? = null
) : UserEvent
