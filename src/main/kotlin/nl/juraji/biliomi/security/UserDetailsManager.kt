package nl.juraji.biliomi.security

import nl.juraji.biliomi.security.repositories.SyncUserPrincipalRepository
import nl.juraji.biliomi.security.repositories.UserGroupRepository
import nl.juraji.biliomi.utils.Validate
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import org.springframework.security.provisioning.GroupManager as SpringGroupManager
import org.springframework.security.provisioning.UserDetailsManager as SpringUserDetailsManager

@Service
class UserDetailsManager(
        private val userPrincipalRepository: SyncUserPrincipalRepository,
        private val groupRepository: UserGroupRepository,
        private val passwordEncoder: PasswordEncoder,
) : SpringUserDetailsManager, SpringGroupManager {

    override fun loadUserByUsername(username: String): UserPrincipal {
        return userPrincipalRepository.findByUsername(username)
                .orElseThrow { usernameNotFound(username) }
    }

    fun createUser(
            username: String,
            password: String,
    ): UserPrincipal {
        Validate.isNotBlank(username) { "Username may not be empty or null" }

        val user = UserPrincipal(
                userId = generateId(),
                username = username,
                password = passwordEncoder.encode(password),
        )

        return userPrincipalRepository.save(user)
    }


    @Deprecated(
            "Deprecated in favor of saveUser",
            replaceWith = ReplaceWith("saveUser(user)")
    )
    override fun createUser(user: UserDetails) {
        Validate.isTrue(user is UserPrincipal) { "Expected UserPrincipal got ${user::class.simpleName}" }
        this.saveUser(user as UserPrincipal)
    }

    @Deprecated(
            "Deprecated in favor of saveUser",
            replaceWith = ReplaceWith("saveUser(user)")
    )
    override fun updateUser(user: UserDetails) {
        Validate.isTrue(user is UserPrincipal) { "Expected UserPrincipal got ${user::class.simpleName}" }
        this.saveUser(user as UserPrincipal)
    }

    fun saveUser(user: UserPrincipal): UserPrincipal {
        Validate.isNotBlank(user.username) { "Username may not be empty or null" }
        Validate.isNotEmpty(user.authorities) { "User authorities may be empty" }
        Validate.isTrue(user.authorities.all { it.authority.isNotBlank() }) { "User authorities may not contain empty values" }

        return userPrincipalRepository.save(user)
    }

    override fun deleteUser(username: String) = userPrincipalRepository.deleteByUsername(username)

    override fun changePassword(oldPassword: String, newPassword: String) {
        val currentUser = SecurityContextHolder.getContext().authentication
        Validate.isNotNull(currentUser) { "No authorization found in context for current user" }

        val principal = userPrincipalRepository.findByUsername(currentUser.name)
                .map { it.copy(password = passwordEncoder.encode(newPassword)) }
                .map(userPrincipalRepository::save)
                .orElseThrow { usernameNotFound(currentUser.name) }

        SecurityContextHolder.getContext().authentication =
                UsernamePasswordAuthenticationToken(principal, null, principal.authorities)
                        .apply { details = currentUser.details }
    }

    override fun userExists(username: String): Boolean = userPrincipalRepository.existsByUsername(username)

    override fun findAllGroups(): List<String> = groupRepository.findAll().map(AuthorityGroup::name)

    override fun findUsersInGroup(groupName: String): MutableList<String> {
        TODO("Not yet implemented")
    }

    override fun createGroup(groupName: String, authorities: List<GrantedAuthority>) {
        Validate.isNotBlank(groupName) { "Group name should not be blank" }
        Validate.isNotEmpty(authorities) { "Group authorities may be empty" }
        Validate.isTrue(authorities.all { it.authority.isNotBlank() }) { "Group authorities may not contain empty values" }

        val group = AuthorityGroup(
                groupId = generateId(),
                name = groupName,
                authorities = authorities
                        .map(GrantedAuthority::getAuthority)
                        .toSet()
        )
        groupRepository.save(group)
    }

    override fun deleteGroup(groupName: String) = groupRepository.deleteById(groupName)

    override fun renameGroup(oldName: String, newName: String) {
        groupRepository.findByName(oldName)
                .map { it.copy(name = newName) }
                .map(groupRepository::save)
                .orElseThrow { groupNotFound(oldName) }
    }

    override fun addUserToGroup(username: String, groupName: String) {
        val user = userPrincipalRepository.findByUsername(username)
                .orElseThrow { UsernameNotFoundException("User $username not found") }
        val group = groupRepository.findByName(groupName)
                .orElseThrow { GroupNotFoundException("Group $groupName not found") }

        val updatedUser = user.copy(authorityGroups = user.authorityGroups.plus(group))
        userPrincipalRepository.save(updatedUser)
    }

    override fun removeUserFromGroup(username: String, groupName: String) {
        userPrincipalRepository.findByUsername(username)
                .map { it.copy(authorityGroups = it.authorityGroups.filter { g -> g.name != groupName }.toSet()) }
                .orElseThrow { UsernameNotFoundException("User $username not found") }
    }

    override fun findGroupAuthorities(groupName: String): List<GrantedAuthority> =
            groupRepository.findByName(groupName)
                    .map { it.authorities.map { GrantedAuthority { it } } }
                    .orElseThrow { groupNotFound(groupName) }

    override fun addGroupAuthority(groupName: String, authority: GrantedAuthority) {
        groupRepository.findByName(groupName)
                .map { it.copy(authorities = it.authorities.plus(authority.authority)) }
                .map(groupRepository::save)
                .orElseThrow { groupNotFound(groupName) }
    }

    override fun removeGroupAuthority(groupName: String, authority: GrantedAuthority) {
        groupRepository.findByName(groupName)
                .map { it.copy(authorities = it.authorities.minus(authority.authority)) }
                .map(groupRepository::save)
                .orElseThrow { groupNotFound(groupName) }
    }

    companion object {
        fun generateId(): String = UUID.randomUUID().toString()
        fun usernameNotFound(username: String): AuthenticationException = UsernameNotFoundException("User $username not found")
        fun groupNotFound(groupName: String): GroupNotFoundException = GroupNotFoundException("Group $groupName not found")
    }

    class GroupNotFoundException(msg: String) : AuthenticationException(msg)
}
