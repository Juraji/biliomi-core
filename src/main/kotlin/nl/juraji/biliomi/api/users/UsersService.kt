package nl.juraji.biliomi.api.users

import nl.juraji.biliomi.security.AuthorityGroup
import nl.juraji.biliomi.security.UserPrincipal
import nl.juraji.biliomi.security.repositories.UserPrincipalRepository
import nl.juraji.biliomi.utils.ValidateAsync
import nl.juraji.biliomi.utils.extensions.uuid
import nl.juraji.biliomi.utils.validate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class UsersService(
        private val userPrincipalRepository: UserPrincipalRepository,
        private val passwordEncoder: PasswordEncoder,
) {

    fun findUsers(): Flux<UserPrincipal> = userPrincipalRepository.findAll()

    fun findUser(userId: String): Mono<UserPrincipal> = userPrincipalRepository.findById(userId)

    fun createUser(username: String, password: String): Mono<UserPrincipal> = ValidateAsync
            .all(
                    ValidateAsync.isFalse(userPrincipalRepository.existsByUsername(username)) { "A user with name $username already exists" },
                    ValidateAsync.all {
                        isNotBlank(username) { "Username may not be empty" }
                        isNotBlank(password) { "Password may not be empty" }
                    }
            )
            .map {
                UserPrincipal(
                        userId = uuid(),
                        username = username,
                        password = passwordEncoder.encode(password),
                )
            }
            .flatMap(userPrincipalRepository::save)

    fun deleteUser(userId: String): Mono<UserPrincipal> = userPrincipalRepository.deleteById(userId)

    fun addGroupToUser(userId: String, authorityGroup: AuthorityGroup): Mono<UserPrincipal> = userPrincipalRepository
            .findById(userId)
            .validate { p ->
                isFalse(p.authorityGroups.any { g -> g.groupId == authorityGroup.groupId }) { "User ${p.username} is already in group ${authorityGroup.name}" }
            }
            .map {
                it.copy(
                        authorityGroups = it.authorityGroups.plus(authorityGroup)
                )
            }
            .flatMap(userPrincipalRepository::save)

    fun removeGroupFromUser(userId: String, groupId: String): Mono<UserPrincipal> = userPrincipalRepository
            .findById(userId)
            .validate { p ->
                val group = p.authorityGroups.find { g -> g.groupId == groupId }
                isNotNull(group) { "User ${p.username} is not in group with id $groupId" }
            }

    fun updateUsername(userId: String, newUsername: String): Mono<UserPrincipal> = ValidateAsync
            .all(
                    ValidateAsync.isFalse(userPrincipalRepository.existsByUsername(newUsername)) { "A user with name $newUsername already exists" },
                    ValidateAsync.all { isNotBlank(newUsername) { "Username may not be empty" } }
            )
            .flatMap { userPrincipalRepository.update(userId) { copy(username = username) } }

    fun updatePassword(userId: String, currentPassword: String, newPassword: String): Mono<UserPrincipal> = userPrincipalRepository
            .findById(userId)
            .validate {
                isNotBlank(currentPassword) { "Current password may not be blank" }
                isNotBlank(newPassword) { "New password may not be blank" }
                isTrue(passwordEncoder.matches(currentPassword, it.password)) { "Current password is incorrect" }
                isFalse(passwordEncoder.matches(newPassword, it.password)) { "New password matches current password" }
            }
            .map {
                it.copy(
                        password = passwordEncoder.encode(newPassword)
                )
            }
            .flatMap(userPrincipalRepository::save)
}
