package nl.juraji.biliomi.domain.bankaccount.commands

import org.axonframework.modelling.command.TargetAggregateIdentifier

abstract class BankAccountCommand(
    @TargetAggregateIdentifier
    open val userId: String
)
