package nl.juraji.biliomi.api.users

import nl.juraji.biliomi.security.UserPrincipal
import nl.juraji.biliomi.security.repositories.AuthorityGroupRepository
import nl.juraji.biliomi.security.repositories.UserPrincipalRepository
import nl.juraji.biliomi.utils.extensions.uuid
import nl.juraji.reactor.validations.validate
import nl.juraji.reactor.validations.validateAsync
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2

@Service
class UsersService(
        private val userPrincipalRepository: UserPrincipalRepository,
        private val authorityGroupRepository: AuthorityGroupRepository,
        private val passwordEncoder: PasswordEncoder
) {

    fun findUsers(): Flux<UserPrincipal> = userPrincipalRepository.findAll()

    fun findUser(userId: String): Mono<UserPrincipal> = userPrincipalRepository.findById(userId)

    fun createUser(username: String, password: String): Mono<UserPrincipal> =
            validateAsync {
                isFalse(userPrincipalRepository.existsByUsername(username)) { "A user with name $username already exists" }
                synchronous {
                    isNotBlank(username) { "Username may not be empty" }
                    isNotBlank(password) { "Password may not be empty" }
                }
            }
                    .map {
                        UserPrincipal(
                                userId = uuid(),
                                username = username,
                                password = passwordEncoder.encode(password),
                        )
                    }
                    .flatMap(userPrincipalRepository::save)

    fun deleteUser(userId: String): Mono<UserPrincipal> = userPrincipalRepository.deleteById(userId)

    fun addGroupToUser(userId: String, groupId: String): Mono<UserPrincipal> = Mono
            .zip(
                    userPrincipalRepository.findById(userId),
                    authorityGroupRepository.findById(groupId)
            )
            .validate { (p) ->
                isFalse(p.authorityGroups.any { g -> g.groupId == groupId }) { "User ${p.username} is already in group $groupId" }
            }
            .map { (principal, group) -> principal.copy(authorityGroups = principal.authorityGroups.plus(group)) }
            .flatMap(userPrincipalRepository::save)

    fun removeGroupFromUser(userId: String, groupId: String): Mono<UserPrincipal> = userPrincipalRepository
            .findById(userId)
            .validate { p ->
                val group = p.authorityGroups.find { g -> g.groupId == groupId }
                isNotNull(group) { "User ${p.username} is not in group with id $groupId" }
            }

    fun updateUsername(userId: String, newUsername: String): Mono<UserPrincipal> = userPrincipalRepository
            .findById(userId)
            .validateAsync { principal ->
                isFalse(userPrincipalRepository.existsByUsername(newUsername)) { "A user with name $newUsername already exists" }
                synchronous {
                    isFalse(principal.username == newUsername) { "Current username is the same as the new username" }
                    isNotBlank(newUsername) { "Username may not be empty" }
                }
            }
            .map { it.copy(username = newUsername) }
            .flatMap(userPrincipalRepository::save)

    fun updatePassword(userId: String, currentPassword: String, newPassword: String): Mono<UserPrincipal> = userPrincipalRepository
            .findById(userId)
            .validate { principal ->
                isNotBlank(currentPassword) { "Current password may not be blank" }
                isNotBlank(newPassword) { "New password may not be blank" }
                isTrue(passwordEncoder.matches(currentPassword, principal.password)) { "Current password is incorrect" }
                isFalse(passwordEncoder.matches(newPassword, principal.password)) { "New password matches current password" }
            }
            .map { it.copy(password = passwordEncoder.encode(newPassword)) }
            .flatMap(userPrincipalRepository::save)
}
