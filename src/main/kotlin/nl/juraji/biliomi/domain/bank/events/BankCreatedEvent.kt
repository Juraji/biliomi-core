package nl.juraji.biliomi.domain.bank.events

import org.axonframework.serialization.Revision

@Revision("1.0")
data class BankCreatedEvent(
        override val userId: String,
) : BankEvent
