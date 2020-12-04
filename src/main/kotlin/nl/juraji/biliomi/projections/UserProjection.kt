package nl.juraji.biliomi.projections

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.ManyToMany

@Entity
data class UserProjection(
    @Id val username: String,
    val displayName: String,
    @JsonIgnore val passwordHash: String?,
    @ManyToMany(fetch = FetchType.EAGER) val authorityGroups: Set<AuthorityGroupProjection> = emptySet()
) {
    fun toPrincipal(): UserDetails {
        val authorities: List<GrantedAuthority> = authorityGroups
            .flatMap { it.authorities }
            .map(::SimpleGrantedAuthority)
            .toSet()
            .sortedBy { it.authority }

        return User.builder()
            .username(username)
            .password(passwordHash ?: "")
            .authorities(authorities)
            .disabled(passwordHash == null)
            .build()
    }
}
