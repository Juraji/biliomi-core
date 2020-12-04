package nl.juraji.biliomi.projections.repositories

import nl.juraji.biliomi.projections.UserProjection
import nl.juraji.biliomi.utils.ReactiveRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import java.util.*

@Repository
interface SyncUserProjectionRepository : JpaRepository<UserProjection, String> {
    fun existsByUsername(username: String): Boolean
    fun findByUsername(username: String): Optional<UserProjection>
}

@Service
class UserProjectionRepository(
    repository: SyncUserProjectionRepository,
    transactionTemplate: TransactionTemplate,
    @Qualifier("projectionsScheduler") scheduler: Scheduler
) : ReactiveRepository<SyncUserProjectionRepository, UserProjection, String>(
    repository,
    scheduler,
    transactionTemplate
) {
    fun existsByUsername(username: String): Mono<Boolean> = from { existsByUsername(username) }

    fun findByUsername(username: String): Mono<UserProjection> = fromOptional { findByUsername(username) }
}
