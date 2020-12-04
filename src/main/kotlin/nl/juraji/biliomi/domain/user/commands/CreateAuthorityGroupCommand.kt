package nl.juraji.biliomi.domain.user.commands

data class CreateAuthorityGroupCommand(
    override val groupId: String,
    val groupName: String,
    val protected: Boolean = false,
    val authorities: Set<String>
) : AuthorityGroupCommand(groupId)
