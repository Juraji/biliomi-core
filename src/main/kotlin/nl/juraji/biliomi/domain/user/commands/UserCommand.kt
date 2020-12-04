package nl.juraji.biliomi.domain.user.commands

import org.axonframework.modelling.command.TargetAggregateIdentifier

abstract class UserCommand(
    @TargetAggregateIdentifier
    open val username: String
)
