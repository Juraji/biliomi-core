package nl.juraji.biliomi.domain.bank.commands

data class TakeBalanceCommand(
        override val userId: String,
        val amount: Long
) : BankCommand(userId)
