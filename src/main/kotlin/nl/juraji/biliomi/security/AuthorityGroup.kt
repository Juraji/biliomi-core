package nl.juraji.biliomi.security

import javax.persistence.*

@Entity
data class AuthorityGroup(
    @Id val groupId: String,
    @Column(unique = true) val name: String,
    val protected: Boolean = false,
    @ElementCollection(fetch = FetchType.EAGER) val authorities: Set<String>,
)
