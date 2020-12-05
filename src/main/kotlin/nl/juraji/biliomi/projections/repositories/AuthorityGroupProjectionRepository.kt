package nl.juraji.biliomi.projections.repositories

import nl.juraji.biliomi.projections.AuthorityGroupProjection
import nl.juraji.biliomi.utils.ReactiveRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler

interface SyncAuthorityProjectionGroupRepository : JpaRepository<AuthorityGroupProjection, String> {
    fun existsByGroupName(groupName: String): Boolean
    fun findByDefaultIsTrue(): List<AuthorityGroupProjection>
}

@Service
class AuthorityGroupProjectionRepository(
    repository: SyncAuthorityProjectionGroupRepository,
    transactionTemplate: TransactionTemplate,
    @Qualifier("projectionsScheduler") scheduler: Scheduler
) : ReactiveRepository<SyncAuthorityProjectionGroupRepository, AuthorityGroupProjection, String>(
    repository,
    scheduler,
    transactionTemplate
) {
    fun existsByName(groupName: String): Mono<Boolean> = from { existsByGroupName(groupName) }
    fun findByDefaultIsTrue(): Flux<AuthorityGroupProjection> = fromIterator { findByDefaultIsTrue() }
}
