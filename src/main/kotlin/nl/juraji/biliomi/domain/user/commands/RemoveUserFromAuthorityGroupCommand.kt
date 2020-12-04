package nl.juraji.biliomi.domain.user.commands

data class RemoveUserFromAuthorityGroupCommand(
    override val username: String,
    val groupId: String
) : UserCommand(username)
