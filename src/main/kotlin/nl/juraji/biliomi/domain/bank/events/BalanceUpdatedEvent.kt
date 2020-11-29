package nl.juraji.biliomi.domain.bank.events

import org.axonframework.serialization.Revision

@Revision("1.0")
data class BalanceUpdatedEvent(
        override val userId: String,
        val previousBalance: Long,
        val newBalance: Long
) : BankEvent
