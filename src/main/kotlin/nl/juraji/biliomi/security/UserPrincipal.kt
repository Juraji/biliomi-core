package nl.juraji.biliomi.security

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.CredentialsContainer
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.*

@Entity
data class UserPrincipal(
    @Id val userId: String,
    private val enabled: Boolean = true,
    @ManyToMany(fetch = FetchType.EAGER) val authorityGroups: Set<AuthorityGroup> = emptySet(),
    @Column(unique = true) private val username: String,
    @JsonIgnore private var password: String,
) : UserDetails, CredentialsContainer {
    override fun getPassword(): String = password
    override fun getUsername(): String = username
    override fun isEnabled(): Boolean = enabled

    @JsonIgnore
    override fun getAuthorities(): Collection<GrantedAuthority> = authorityGroups
        .flatMap(AuthorityGroup::authorities)
        .map(::SimpleGrantedAuthority)
        .toSet()
        .sortedBy { it.authority }

    @JsonIgnore
    override fun isAccountNonExpired(): Boolean = true

    @JsonIgnore
    override fun isAccountNonLocked(): Boolean = true

    @JsonIgnore
    override fun isCredentialsNonExpired(): Boolean = true

    override fun eraseCredentials() {
        password = ""
    }
}
