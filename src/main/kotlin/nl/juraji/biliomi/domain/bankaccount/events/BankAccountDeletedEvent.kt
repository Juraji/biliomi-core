package nl.juraji.biliomi.domain.bankaccount.events

data class BankAccountDeletedEvent(
    override val accountId: String
) : BankAccountEvent
