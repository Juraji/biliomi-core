package nl.juraji.biliomi.domain.bankaccount.events

import org.axonframework.serialization.Revision

@Revision("1.0")
data class BankAccountCreatedEvent(
    override val userId: String,
) : BankAccountEvent
