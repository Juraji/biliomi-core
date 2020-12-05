package nl.juraji.biliomi.projections

import javax.persistence.*

@Entity
data class AuthorityGroupProjection(
    @Id val groupId: String,
    val groupName: String,
    @Column(name = "isProtected")val protected: Boolean,
    @Column(name = "isDefault")val default: Boolean,
    @ElementCollection(fetch = FetchType.EAGER) val authorities: Set<String>
)
