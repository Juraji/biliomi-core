package nl.juraji.biliomi.domain.user.commands

data class DeleteAuthorityGroupCommand(
    override val groupId: String
) : AuthorityGroupCommand(groupId)
