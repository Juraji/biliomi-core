package nl.juraji.biliomi.domain.user.events

import org.axonframework.serialization.Revision

@Revision("1.0")
data class AuthorityGroupCreatedEvent(
    override val groupId: String,
    val groupName: String,
    val authorities: Set<String>,
    val protected: Boolean,
    val default: Boolean,
) : AuthorityGroupEvent
