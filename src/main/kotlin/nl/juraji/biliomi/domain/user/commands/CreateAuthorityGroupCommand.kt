package nl.juraji.biliomi.domain.user.commands

data class CreateAuthorityGroupCommand(
    override val groupId: String,
    val groupName: String,
    val authorities: Set<String>,
    val protected: Boolean = false
) : AuthorityGroupCommand(groupId)
