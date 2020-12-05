package nl.juraji.biliomi.domain.user.commands

class UpdateAuthorityGroupCommand(
    override val groupId: String,
    val groupName: String,
    val authorities: Set<String>,
    val default: Boolean
) : AuthorityGroupCommand(groupId)
