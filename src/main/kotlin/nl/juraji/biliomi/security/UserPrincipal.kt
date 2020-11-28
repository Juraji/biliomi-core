package nl.juraji.biliomi.security

import org.springframework.security.core.CredentialsContainer
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.*

@Entity
data class UserPrincipal(
        @Id val userId: String,
        @ManyToMany(fetch = FetchType.EAGER)
        val authorityGroups: Set<AuthorityGroup> = emptySet(),

        @Column(unique = true) private val username: String,
        private var password: String,
        private val accountNonExpired: Boolean = true,
        private val accountNonLocked: Boolean = true,
        private val credentialsNonExpired: Boolean = true,
        private val enabled: Boolean = true,
) : UserDetails, CredentialsContainer {
    override fun getAuthorities(): Collection<GrantedAuthority> = authorityGroups
            .flatMap(AuthorityGroup::authorities)
            .map { GrantedAuthority { it } }
    override fun getPassword(): String = password
    override fun getUsername(): String = username
    override fun isAccountNonExpired(): Boolean = accountNonExpired
    override fun isAccountNonLocked(): Boolean = accountNonLocked
    override fun isCredentialsNonExpired(): Boolean = credentialsNonExpired
    override fun isEnabled(): Boolean = enabled

    override fun eraseCredentials() {
        password = ""
    }

    fun eraseCredentialsK(): UserPrincipal = this.copy(password = "")
}
