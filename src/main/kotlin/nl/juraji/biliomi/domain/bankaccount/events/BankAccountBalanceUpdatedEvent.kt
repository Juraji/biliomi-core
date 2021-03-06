package nl.juraji.biliomi.domain.bankaccount.events

import org.axonframework.serialization.Revision

@Revision("1.0")
data class BankAccountBalanceUpdatedEvent(
    override val accountId: String,
    val previousBalance: Long,
    val newBalance: Long,
    val message: String? = null
) : BankAccountEvent
