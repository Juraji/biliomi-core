package nl.juraji.biliomi.domain.bank.commands

data class AddBalanceCommand(
        override val userId: String,
        val amount: Long
) : BankCommand(userId)
