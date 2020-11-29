package nl.juraji.biliomi.domain.bank.commands

data class DeleteBankCommand(
        override val userId: String,
) : BankCommand(userId)
