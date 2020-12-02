package nl.juraji.biliomi.domain.bankaccount.commands

data class DeleteBankAccountCommand(
    override val userId: String,
) : BankAccountCommand(userId)
