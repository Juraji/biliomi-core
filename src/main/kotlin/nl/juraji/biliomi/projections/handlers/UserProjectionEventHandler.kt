package nl.juraji.biliomi.projections.handlers

import nl.juraji.biliomi.configuration.ProcessingGroups
import nl.juraji.biliomi.domain.user.events.*
import nl.juraji.biliomi.projections.UserProjection
import nl.juraji.biliomi.projections.repositories.AuthorityGroupProjectionRepository
import nl.juraji.biliomi.projections.repositories.UserProjectionRepository
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Service

@Service
@ProcessingGroup(ProcessingGroups.PROJECTIONS)
class UserProjectionEventHandler(
    private val userProjectionRepository: UserProjectionRepository,
    private val authorityGroupProjectionRepository: AuthorityGroupProjectionRepository
) {

    @EventHandler
    fun on(e: UserCreatedEvent) {
        val entity = UserProjection(
            username = e.username,
            displayName = e.displayName,
            passwordHash = e.passwordHash
        )

        userProjectionRepository
            .save(entity)
            .block()
    }

    @EventHandler
    fun on(e: UserDisplayNameUpdatedEvent) {
        userProjectionRepository
            .update(e.username) {
                copy(displayName = e.displayName)
            }
            .block()
    }

    @EventHandler
    fun on(e: UserPasswordUpdatedEvent) {
        userProjectionRepository
            .update(e.username) {
                copy(passwordHash = e.passwordHash)
            }
            .block()
    }

    @EventHandler
    fun on(e: UserAddedToAuthorityGroupEvent) {
        authorityGroupProjectionRepository
            .findById(e.groupId)
            .flatMap { group ->
                userProjectionRepository.update(e.username) {
                    copy(authorityGroups = authorityGroups.plus(group))
                }
            }
            .block()
    }

    @EventHandler
    fun on(e: UserRemovedFromAuthorityGroupEvent) {
        userProjectionRepository
            .update(e.username) {
                copy(authorityGroups = authorityGroups.filter { it.groupId == e.groupId }.toSet())
            }
            .block()
    }

    @EventHandler
    fun on(e: UserDeletedEvent) {
        userProjectionRepository
            .deleteById(e.username)
            .block()
    }
}
