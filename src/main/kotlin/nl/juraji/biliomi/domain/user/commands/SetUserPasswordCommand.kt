package nl.juraji.biliomi.domain.user.commands

data class SetUserPasswordCommand(
    override val userId: String,
    val passwordHash: String
) : UserCommand(userId)

