package nl.juraji.biliomi.security

import org.springframework.security.core.GrantedAuthority
import javax.persistence.*

@Entity
data class UserGroup(
        @Id val groupId: String,
        val name: String,
        @Convert(converter = GrantedAuthorityConverter::class)
        @ElementCollection(fetch = FetchType.EAGER) val authorities: Set<GrantedAuthority>
)
