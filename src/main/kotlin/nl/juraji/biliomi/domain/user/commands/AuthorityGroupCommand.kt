package nl.juraji.biliomi.domain.user.commands

import org.axonframework.modelling.command.TargetAggregateIdentifier

abstract class AuthorityGroupCommand(
    @TargetAggregateIdentifier
    open val groupId: String
)
