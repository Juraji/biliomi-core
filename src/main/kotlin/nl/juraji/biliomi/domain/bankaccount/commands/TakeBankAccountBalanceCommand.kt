package nl.juraji.biliomi.domain.bankaccount.commands

data class TakeBankAccountBalanceCommand(
    override val userId: String,
    val amount: Long,
    val message: String? = null,
) : BankAccountCommand(userId)
