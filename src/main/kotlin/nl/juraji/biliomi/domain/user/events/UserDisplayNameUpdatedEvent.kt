package nl.juraji.biliomi.domain.user.events

import org.axonframework.serialization.Revision

@Revision("1.0")
data class UserDisplayNameUpdatedEvent(
    override val username: String,
    val displayName: String,
) : UserEvent
