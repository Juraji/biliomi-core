package nl.juraji.biliomi.domain.user.commands

import nl.juraji.biliomi.domain.user.UserId

data class AddPointsCommand(
        override val userId: UserId,
        val amount: Long,
) : UserCommand(userId)
