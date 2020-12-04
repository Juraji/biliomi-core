package nl.juraji.biliomi.domain.user.commands

data class SetAuthorityGroupAuthoritiesCommand(
    override val groupId: String,
    val authorities: Set<String>
) : AuthorityGroupCommand(groupId)
