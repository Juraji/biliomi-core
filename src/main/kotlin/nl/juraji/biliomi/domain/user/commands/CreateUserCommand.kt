package nl.juraji.biliomi.domain.user.commands

data class CreateUserCommand(
    override val userId: String,
    val username: String,
    val passwordHash: String? = null,
) : UserCommand(userId)
