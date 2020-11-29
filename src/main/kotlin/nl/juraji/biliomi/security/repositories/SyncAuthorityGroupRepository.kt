package nl.juraji.biliomi.security.repositories

import nl.juraji.biliomi.security.AuthorityGroup
import nl.juraji.biliomi.utils.ReactiveRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler

@Repository
interface SyncAuthorityGroupRepository : JpaRepository<AuthorityGroup, String> {
    fun existsByName(groupName: String): Boolean
}

@Service
class AuthorityGroupRepository(
        syncAuthorityGroupRepository: SyncAuthorityGroupRepository,
        transactionTemplate: TransactionTemplate,
        @Qualifier("securityScheduler") scheduler: Scheduler,
) : ReactiveRepository<SyncAuthorityGroupRepository, AuthorityGroup, String>(
        syncAuthorityGroupRepository,
        scheduler,
        transactionTemplate
) {
    fun existsByName(groupName: String): Mono<Boolean> = from { existsByName(groupName) }
}
