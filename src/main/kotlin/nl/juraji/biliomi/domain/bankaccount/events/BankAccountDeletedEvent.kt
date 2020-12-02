package nl.juraji.biliomi.domain.bankaccount.events

data class BankAccountDeletedEvent(
    override val userId: String
) : BankAccountEvent
