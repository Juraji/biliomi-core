package nl.juraji.biliomi.domain.user

import nl.juraji.biliomi.configuration.security.Authorities
import nl.juraji.biliomi.domain.user.commands.CreateAuthorityGroupCommand
import nl.juraji.biliomi.domain.user.commands.DeleteAuthorityGroupCommand
import nl.juraji.biliomi.domain.user.commands.SetAuthorityGroupAuthoritiesCommand
import nl.juraji.biliomi.domain.user.commands.SetAuthorityGroupNameCommand
import nl.juraji.biliomi.domain.user.events.AuthorityGroupCreatedEvent
import nl.juraji.biliomi.domain.user.events.AuthorityGroupDeletedEvent
import nl.juraji.biliomi.domain.user.events.AuthorityGroupUpdatedEvent
import nl.juraji.reactor.validations.validate
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class AuthorityGroupAggregate() {

    @AggregateIdentifier
    private lateinit var groupId: String
    private lateinit var groupName: String
    private var protected: Boolean = false
    private var authorities: Set<String> = emptySet()

    @CommandHandler
    constructor(cmd: CreateAuthorityGroupCommand) : this() {
        validate {
            isNotBlank(cmd.groupId) { "Group id should not be blank" }
            isNotBlank(cmd.groupName) { "Group name should not be blank" }
            isNotEmpty(cmd.authorities) { "Group authorities may not be empty" }
            isTrue(cmd.authorities.all(String::isNotBlank)) { "Group authorities may not contain empty values" }
            isTrue(cmd.authorities.all(Authorities.all::contains)) { "Unknown entry found in authorities" }
        }

        AggregateLifecycle.apply(
            AuthorityGroupCreatedEvent(
                groupId = cmd.groupId,
                groupName = cmd.groupName,
                protected = cmd.protected,
                authorities = cmd.authorities,
            )
        )
    }

    @CommandHandler
    fun handle(cmd: SetAuthorityGroupNameCommand) {
        validate {
            isNotBlank(cmd.groupName) { "Group name should not be blank" }
            isFalse(cmd.groupName == groupName) { "Group name did not change" }
        }

        AggregateLifecycle.apply(
            AuthorityGroupUpdatedEvent(
                groupId = groupId,
                groupName = cmd.groupName,
                authorities = authorities
            )
        )
    }

    @CommandHandler
    fun handle(cmd: SetAuthorityGroupAuthoritiesCommand) {
        validate {
            isFalse(protected) { "Authority group $groupName is protected and may not have its authorities updated" }
            isNotEmpty(cmd.authorities) { "Group authorities may not be empty" }
            isTrue(cmd.authorities.all(String::isNotBlank)) { "Group authorities may not contain empty values" }
            isTrue(cmd.authorities.all(Authorities.all::contains)) { "Unknown entry found in authorities" }
        }

        AggregateLifecycle.apply(
            AuthorityGroupUpdatedEvent(
                groupId = groupId,
                groupName = groupName,
                authorities = cmd.authorities
            )
        )
    }

    @CommandHandler
    fun handle(cmd: DeleteAuthorityGroupCommand) {
        validate {
            isFalse(protected) { "Authority group $groupName is protected and can not be deleted" }
        }

        AggregateLifecycle.apply(
            AuthorityGroupDeletedEvent(
                groupId = groupId
            )
        )
    }

    @EventSourcingHandler
    fun on(e: AuthorityGroupCreatedEvent) {
        this.groupId = e.groupId
        this.groupName = e.groupName
        this.protected = e.protected
        this.authorities = e.authorities
    }

    @EventSourcingHandler
    fun on(e: AuthorityGroupUpdatedEvent) {
        this.groupName = e.groupName
        this.authorities = e.authorities
    }

    @EventSourcingHandler
    fun on(e: AuthorityGroupDeletedEvent) {
        AggregateLifecycle.markDeleted()
    }
}
