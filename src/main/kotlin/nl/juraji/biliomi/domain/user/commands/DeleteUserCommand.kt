package nl.juraji.biliomi.domain.user.commands

data class DeleteUserCommand(
    override val username: String
) : UserCommand(username)
