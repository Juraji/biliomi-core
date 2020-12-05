package nl.juraji.biliomi.domain.user.events

import nl.juraji.biliomi.domain.DomainEvent
import org.axonframework.serialization.Revision

@Revision("1.0")
interface AuthorityGroupEvent: DomainEvent{
    val groupId: String
}
