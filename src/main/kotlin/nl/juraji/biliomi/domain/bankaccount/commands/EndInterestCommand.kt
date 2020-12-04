package nl.juraji.biliomi.domain.bankaccount.commands

data class EndInterestCommand(
    override val accountId: String
) : BankAccountCommand(accountId)
