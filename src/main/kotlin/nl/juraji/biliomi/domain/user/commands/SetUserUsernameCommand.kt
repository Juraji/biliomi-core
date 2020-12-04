package nl.juraji.biliomi.domain.user.commands

data class SetUserUsernameCommand(
    override val userId: String,
    val username: String,
): UserCommand(userId)
