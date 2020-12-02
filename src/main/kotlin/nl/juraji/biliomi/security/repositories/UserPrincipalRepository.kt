package nl.juraji.biliomi.security.repositories

import nl.juraji.biliomi.security.UserPrincipal
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
interface SyncUserPrincipalRepository : JpaRepository<UserPrincipal, String> {
    fun findByUsername(username: String): Optional<UserPrincipal>
    fun existsByUsername(username: String): Boolean
}

@Service
class UserPrincipalRepository(
    syncUserPrincipalRepository: SyncUserPrincipalRepository,
    transactionTemplate: TransactionTemplate,
    @Qualifier("securityScheduler") scheduler: Scheduler,
) : ReactiveRepository<SyncUserPrincipalRepository, UserPrincipal, String>(
    syncUserPrincipalRepository,
    scheduler,
    transactionTemplate
) {
    fun findByUsername(username: String): Mono<UserPrincipal> = fromOptional { findByUsername(username) }
    fun existsByUsername(username: String): Mono<Boolean> = from { existsByUsername(username) }
}
