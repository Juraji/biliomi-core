package nl.juraji.biliomi.domain.bank.commands

import org.axonframework.modelling.command.TargetAggregateIdentifier

abstract class BankCommand(
        @TargetAggregateIdentifier
        open val userId: String
)
