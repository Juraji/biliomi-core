package nl.juraji.biliomi.projections

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class BankProjection(
    @Id val accountId: String,
    val userId: String,
    val balance: Long = 0
)
