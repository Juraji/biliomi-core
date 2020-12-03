package nl.juraji.biliomi.domain.bankaccount.events

data class InterestEndedEvent(
    override val userId: String
) : BankAccountEvent
