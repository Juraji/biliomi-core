package nl.juraji.biliomi.domain.user.events

import nl.juraji.biliomi.domain.user.UserId

interface UserEvent {
    val userId: UserId
}
