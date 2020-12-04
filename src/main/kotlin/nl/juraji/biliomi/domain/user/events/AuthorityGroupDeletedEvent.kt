package nl.juraji.biliomi.domain.user.events

data class AuthorityGroupDeletedEvent(
    override val groupId: String
) : AuthorityGroupEvent
