package nl.juraji.biliomi.domain.user.events

import nl.juraji.biliomi.domain.DomainEvent

interface UserEvent : DomainEvent {
    val userId: String
}
