package nl.juraji.biliomi.domain.user.commands

data class SetUserPasswordCommand(
    override val username: String,
    val passwordHash: String
) : UserCommand(username)

