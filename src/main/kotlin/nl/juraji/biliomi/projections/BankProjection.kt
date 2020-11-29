package nl.juraji.biliomi.projections

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class BankProjection(
        @Id val userId: String,
        val balance: Long = 0
)
