package nl.juraji.biliomi.projections

import org.springframework.data.annotation.Id
import java.time.Instant

data class UserProjection(
        @Id val id: String,
        val username: String,
        val createdAt: Instant,
        val pointsBalance: Long = 0,
)
