package nl.juraji.biliomi.domain.bankaccount.commands

data class CreateBankAccountCommand(
    override val accountId: String,
    val userId: String
) : BankAccountCommand(accountId)
