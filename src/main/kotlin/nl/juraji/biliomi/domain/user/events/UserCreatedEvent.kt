package nl.juraji.biliomi.domain.user.events

import nl.juraji.biliomi.domain.user.UserId

class UserCreatedEvent(
        override val userId: UserId,
        val username: String,
) : UserEvent
