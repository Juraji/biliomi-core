package nl.juraji.biliomi.domain.bank.events

data class BankDeletedEvent(
        override val userId: String
) : BankEvent
