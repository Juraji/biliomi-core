package nl.juraji.biliomi.domain.user.commands

data class SetAuthorityGroupNameCommand(
    override val groupId: String,
    val groupName: String
) : AuthorityGroupCommand(groupId)
