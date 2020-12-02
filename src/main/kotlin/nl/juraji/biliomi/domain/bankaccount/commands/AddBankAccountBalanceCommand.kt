package nl.juraji.biliomi.domain.bankaccount.commands

data class AddBankAccountBalanceCommand(
    override val userId: String,
    val amount: Long
) : BankAccountCommand(userId)
