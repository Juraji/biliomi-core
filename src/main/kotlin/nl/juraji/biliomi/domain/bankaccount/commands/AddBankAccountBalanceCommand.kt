package nl.juraji.biliomi.domain.bankaccount.commands

data class AddBankAccountBalanceCommand(
    override val accountId: String,
    val amount: Long,
    val message: String? = null,
) : BankAccountCommand(accountId)
