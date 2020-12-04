package nl.juraji.biliomi.projections

import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id

@Entity
data class AuthorityGroupProjection(
    @Id val groupId: String,
    val groupName: String,
    @ElementCollection(fetch = FetchType.EAGER) val authorities: Set<String>
)
