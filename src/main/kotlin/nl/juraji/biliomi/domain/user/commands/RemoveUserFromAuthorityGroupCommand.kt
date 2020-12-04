package nl.juraji.biliomi.domain.user.commands

data class RemoveUserFromAuthorityGroupCommand(
    override val userId: String,
    val groupId: String
) : UserCommand(userId)
