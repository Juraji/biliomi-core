package nl.juraji.biliomi.domain.user.commands

import nl.juraji.biliomi.domain.user.UserId

data class CreateUserCommand(
        override val userId: UserId,
        val username: String,
) : UserCommand(userId)
