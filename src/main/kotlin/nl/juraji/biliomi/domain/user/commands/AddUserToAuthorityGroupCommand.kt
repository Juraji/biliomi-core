package nl.juraji.biliomi.domain.user.commands

data class AddUserToAuthorityGroupCommand(
    override val userId: String,
    val groupId: String
) : UserCommand(userId)
