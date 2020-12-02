package nl.juraji.biliomi.domain.user.commands

data class DeleteUserCommand(
    override val userId: String
) : UserCommand(userId)
