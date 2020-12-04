package nl.juraji.biliomi.projections.repositories

import nl.juraji.biliomi.projections.BankProjection
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
interface SyncBankProjectionRepository : JpaRepository<BankProjection, String> {
    fun findByUsername(username: String): Optional<BankProjection>
}

@Service
class BankProjectionRepository(
    repository: SyncBankProjectionRepository,
    transactionTemplate: TransactionTemplate,
    @Qualifier("projectionsScheduler") scheduler: Scheduler
) : ReactiveRepository<SyncBankProjectionRepository, BankProjection, String>(
    repository,
    scheduler,
    transactionTemplate
) {
    fun findByUsername(username: String): Mono<BankProjection> = fromOptional { findByUsername(username) }
}
