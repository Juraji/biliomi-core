package nl.juraji.biliomi.domain.user.events

import nl.juraji.biliomi.domain.DomainEvent

interface AuthorityGroupEvent: DomainEvent{
    val groupId: String
}
