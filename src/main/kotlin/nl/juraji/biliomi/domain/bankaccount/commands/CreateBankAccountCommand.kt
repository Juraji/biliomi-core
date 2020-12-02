package nl.juraji.biliomi.domain.bankaccount.commands

data class CreateBankAccountCommand(
    override val userId: String,
) : BankAccountCommand(userId)
