package nl.juraji.biliomi.projections

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.CredentialsContainer
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.*

@Entity
data class UserProjection(
    @Id val userId: String,
    @Column(unique = true) val username: String,
    @JsonIgnore val passwordHash: String?,
    @ManyToMany(fetch = FetchType.EAGER) val authorityGroups: Set<AuthorityGroupProjection> = emptySet()
) {
    fun toPrincipal(): UserDetails {
        val authorities: List<GrantedAuthority> = authorityGroups
            .flatMap { it.authorities }
            .map(::SimpleGrantedAuthority)
            .toSet()
            .sortedBy { it.authority }

        return UserPrincipal(
            userId = userId,
            username = username,
            enabled = passwordHash != null,
            password = passwordHash ?: "",
            authorities = authorities
        )
    }
}

data class UserPrincipal(
    val userId: String,
    private val authorities: List<GrantedAuthority>,
    private val username: String,
    private var password: String,
    private val enabled: Boolean
) : UserDetails, CredentialsContainer {
    override fun getPassword(): String = password
    override fun getUsername(): String = username
    override fun isEnabled(): Boolean = enabled
    override fun getAuthorities(): Collection<GrantedAuthority> = authorities
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun eraseCredentials() {
        password = ""
    }
}
