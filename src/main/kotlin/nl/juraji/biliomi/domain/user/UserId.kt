package nl.juraji.biliomi.domain.user

import nl.juraji.biliomi.domain.EntityId
import org.axonframework.common.IdentifierFactory

data class UserId(
        override val identifier: String = IdentifierFactory.getInstance().generateIdentifier(),
) : EntityId
