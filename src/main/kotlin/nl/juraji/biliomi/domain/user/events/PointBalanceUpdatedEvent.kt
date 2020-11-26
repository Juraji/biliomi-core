package nl.juraji.biliomi.domain.user.events

import nl.juraji.biliomi.domain.user.UserId

data class PointBalanceUpdatedEvent(
        override val userId: UserId,
        val previousBalance: Long,
        val newBalance: Long,
): UserEvent
