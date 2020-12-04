package nl.juraji.biliomi.projections.handlers

import nl.juraji.biliomi.configuration.ProcessingGroups
import nl.juraji.biliomi.domain.user.events.AuthorityGroupCreatedEvent
import nl.juraji.biliomi.domain.user.events.AuthorityGroupDeletedEvent
import nl.juraji.biliomi.domain.user.events.AuthorityGroupUpdatedEvent
import nl.juraji.biliomi.projections.AuthorityGroupProjection
import nl.juraji.biliomi.projections.repositories.AuthorityGroupProjectionRepository
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Service

@Service
@ProcessingGroup(ProcessingGroups.PROJECTIONS)
class AuthorityGroupProjectionEventHandler(
    private val authorityGroupRepository: AuthorityGroupProjectionRepository
) {

    @EventHandler
    fun on(e: AuthorityGroupCreatedEvent) {
        val entity = AuthorityGroupProjection(
            groupId = e.groupId,
            groupName = e.groupName,
            authorities = e.authorities
        )

        authorityGroupRepository
            .save(entity)
            .block()
    }

    @EventHandler
    fun on(e: AuthorityGroupUpdatedEvent) {
        authorityGroupRepository
            .update(e.groupId) {
                copy(
                    groupName = e.groupName,
                    authorities = e.authorities
                )
            }
            .block()
    }

    @EventHandler
    fun on(e: AuthorityGroupDeletedEvent) {
        authorityGroupRepository
            .deleteById(e.groupId)
            .block()
    }
}
