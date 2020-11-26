package nl.juraji.biliomi.domain.user.commands

import nl.juraji.biliomi.domain.user.UserId
import org.axonframework.modelling.command.TargetAggregateIdentifier

abstract class UserCommand(
        @TargetAggregateIdentifier
        open val userId: UserId,
)
