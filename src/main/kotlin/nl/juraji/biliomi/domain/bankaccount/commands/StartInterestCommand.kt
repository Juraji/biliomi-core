package nl.juraji.biliomi.domain.bankaccount.commands

data class StartInterestCommand(
    override val userId: String
) : BankAccountCommand(userId)
