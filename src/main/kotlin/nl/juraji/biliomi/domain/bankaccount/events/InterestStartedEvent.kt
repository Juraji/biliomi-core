package nl.juraji.biliomi.domain.bankaccount.events

data class InterestStartedEvent(
    override val accountId: String
) : BankAccountEvent
