package nl.juraji.biliomi.projections.handlers

import nl.juraji.biliomi.configuration.ProcessingGroups
import nl.juraji.biliomi.domain.user.events.PointBalanceUpdatedEvent
import nl.juraji.biliomi.domain.user.events.UserCreatedEvent
import nl.juraji.biliomi.projections.UserProjection
import nl.juraji.biliomi.projections.repositories.UserRepository
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.Timestamp
import org.springframework.stereotype.Service
import java.time.Instant

@Service
@ProcessingGroup(ProcessingGroups.PROJECTIONS)
class UserProjectionsEventHandler(
        private val userRepository: UserRepository,
) {

    @EventHandler
    fun on(e: UserCreatedEvent, @Timestamp timestamp: Instant) {
        val entity = UserProjection(
                id = e.userId.identifier,
                username = e.username,
                createdAt = timestamp,
        )

        userRepository.insert(entity).block()
    }

    @EventHandler
    fun on(e: PointBalanceUpdatedEvent) {
        userRepository.findById(e.userId.identifier)
                .map { it.copy(pointsBalance = e.newBalance) }
                .flatMap { userRepository.save(it) }
                .block()
    }
}
