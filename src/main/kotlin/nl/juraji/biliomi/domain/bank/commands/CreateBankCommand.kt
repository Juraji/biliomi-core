package nl.juraji.biliomi.domain.bank.commands

data class CreateBankCommand(
        override val userId: String,
) : BankCommand(userId)
