package nl.juraji.biliomi.domain.bankaccount.commands

data class DeleteBankAccountCommand(
    override val accountId: String,
) : BankAccountCommand(accountId)
