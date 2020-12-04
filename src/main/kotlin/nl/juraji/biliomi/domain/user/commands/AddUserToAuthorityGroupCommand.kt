package nl.juraji.biliomi.domain.user.commands

data class AddUserToAuthorityGroupCommand(
    override val username: String,
    val groupId: String
) : UserCommand(username)
