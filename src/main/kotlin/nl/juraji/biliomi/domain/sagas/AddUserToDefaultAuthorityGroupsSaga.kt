package nl.juraji.biliomi.domain.sagas

import nl.juraji.biliomi.configuration.ProcessingGroups
import nl.juraji.biliomi.domain.user.commands.AddUserToAuthorityGroupCommand
import nl.juraji.biliomi.domain.user.events.UserAddedToAuthorityGroupEvent
import nl.juraji.biliomi.domain.user.events.UserCreatedEvent
import nl.juraji.biliomi.projections.repositories.SyncAuthorityProjectionGroupRepository
import nl.juraji.biliomi.utils.LoggerCompanion
import nl.juraji.biliomi.utils.SagaAssociations
import org.axonframework.config.ProcessingGroup
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.serialization.Revision
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.core.publisher.toFlux
import java.time.Duration

@Saga
@Revision("1.0")
@ProcessingGroup(ProcessingGroups.SAGA)
class AddUserToDefaultAuthorityGroupsSaga {

    @Transient
    @Autowired
    private lateinit var commandGateway: ReactorCommandGateway

    @Transient
    @Autowired
    private lateinit var authorityGroupRepository: SyncAuthorityProjectionGroupRepository

    @StartSaga
    @SagaEventHandler(associationProperty = ASSOC_USER)
    fun on(e: UserCreatedEvent) {
        val defaultGroups = authorityGroupRepository.findByDefaultIsTrue()

        if (defaultGroups.isEmpty()) {
            SagaLifecycle.end()
        } else {
            defaultGroups.forEach { SagaAssociations.associateWith(ASSOC_GROUP, it.groupId) }

            val linkCmds = defaultGroups.toFlux()
                .delayElements(Duration.ofSeconds(1))
                .map { AddUserToAuthorityGroupCommand(e.username, it.groupId) }

            commandGateway.sendAll(linkCmds).blockLast()
        }
    }

    @SagaEventHandler(associationProperty = ASSOC_GROUP)
    fun on(e: UserAddedToAuthorityGroupEvent) {
        SagaAssociations.removeAssociationWith(ASSOC_GROUP, e.groupId)
        logger.info("Added user ${e.username} to default authority group ${e.groupId}")

        if (!SagaAssociations.hasAssociationKey(ASSOC_GROUP)) {
            logger.debug("Added all default groups to ${e.username}, saga ended")
            SagaLifecycle.end()
        }
    }

    companion object : LoggerCompanion(AddUserToDefaultAuthorityGroupsSaga::class) {
        const val ASSOC_GROUP = "groupId"
        const val ASSOC_USER = "username"
    }
}
