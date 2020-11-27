package nl.juraji.biliomi.projections.repositories

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

interface R2dbcRepositoryExt<T : Any> {
    fun <S : T> insert(entity: S): Mono<S>
    fun <S : T> update(entity: S): Mono<S>
}

@Repository
@Transactional(readOnly = true)
class R2dbcRepositoryExtImpl<T : Any>(
        @Qualifier("projectionsEntityTemplate") private val operations: R2dbcEntityOperations,
) : R2dbcRepositoryExt<T> {
    override fun <S : T> insert(entity: S): Mono<S> = operations.insert(entity)
    override fun <S : T> update(entity: S): Mono<S> = operations.update(entity)
}
