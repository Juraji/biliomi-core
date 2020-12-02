package nl.juraji.biliomi.domain.user.commands

data class CreateUserCommand(
    override val userId: String
) : UserCommand(userId)
