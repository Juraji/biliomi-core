package nl.juraji.biliomi.domain.bankaccount.commands

data class StartInterestCommand(
    override val accountId: String
) : BankAccountCommand(accountId)
