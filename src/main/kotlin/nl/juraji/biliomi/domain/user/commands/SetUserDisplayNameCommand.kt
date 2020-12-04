package nl.juraji.biliomi.domain.user.commands

data class SetUserDisplayNameCommand(
    override val username: String,
    val displayName: String,
): UserCommand(username)
