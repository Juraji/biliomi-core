package nl.juraji.biliomi.domain.user.events

import org.axonframework.serialization.Revision

@Revision("1.0")
data class UserDeletedEvent(
    override val username: String
) : UserEvent
