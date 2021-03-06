package nl.juraji.biliomi.domain.user.commands

data class CreateUserCommand(
    override val username: String,
    val displayName: String,
    val passwordHash: String? = null,
) : UserCommand(username)
