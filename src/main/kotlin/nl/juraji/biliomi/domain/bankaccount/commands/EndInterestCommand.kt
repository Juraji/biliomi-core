package nl.juraji.biliomi.domain.bankaccount.commands

data class EndInterestCommand(
    override val userId: String
) : BankAccountCommand(userId)
