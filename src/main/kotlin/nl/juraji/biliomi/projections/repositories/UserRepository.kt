package nl.juraji.biliomi.projections.repositories

import nl.juraji.biliomi.projections.UserProjection
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface UserRepository : R2dbcRepository<UserProjection, String>, R2dbcRepositoryExt<UserProjection> {
}
