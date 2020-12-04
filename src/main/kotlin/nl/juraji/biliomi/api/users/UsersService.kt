package nl.juraji.biliomi.api.users

import nl.juraji.biliomi.api.ReactiveWebContext
import nl.juraji.biliomi.domain.user.commands.*
import nl.juraji.biliomi.projections.UserProjection
import nl.juraji.biliomi.projections.repositories.AuthorityGroupProjectionRepository
import nl.juraji.biliomi.projections.repositories.UserProjectionRepository
import nl.juraji.biliomi.utils.extensions.uuid
import nl.juraji.reactor.validations.ValidationException
import nl.juraji.reactor.validations.validate
import nl.juraji.reactor.validations.validateAsync
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class UsersService(
    private val userRepository: UserProjectionRepository,
    private val authorityGroupRepository: AuthorityGroupProjectionRepository,
    private val passwordEncoder: PasswordEncoder,
    private val commandGateway: ReactorCommandGateway
) {

    fun findUsers(): Flux<UserProjection> = userRepository.findAll()

    fun findUser(userId: String): Mono<UserProjection> = userRepository.findById(userId)

    fun createUser(userId: String?, username: String, password: String?): Mono<String> =
        validateAsync { isFalse(userRepository.existsByUsername(username)) { "A user with name $username already exists" } }
            .flatMap {
                commandGateway.send(CreateUserCommand(
                    userId = userId ?: uuid(),
                    username,
                    passwordHash = password?.let { passwordEncoder.encode(it) }
                ))
            }

    fun deleteUser(userId: String): Mono<Unit> = commandGateway.send(DeleteUserCommand(userId))

    fun addGroupToUser(userId: String, groupId: String): Mono<Unit> = authorityGroupRepository
        .findById(groupId)
        .onErrorMap { ValidationException("Authority group with id $groupId does not exist") }
        .flatMap { commandGateway.send(AddUserToAuthorityGroupCommand(userId, groupId)) }

    fun removeGroupFromUser(userId: String, groupId: String): Mono<Unit> =
        commandGateway.send(RemoveUserFromAuthorityGroupCommand(userId, groupId))

    fun updateUsername(userId: String, newUsername: String): Mono<Unit> =
        validateAsync { isFalse(userRepository.existsByUsername(newUsername)) { "A user with name $newUsername already exists" } }
            .flatMap { commandGateway.send(SetUserUsernameCommand(userId, newUsername)) }

    fun updatePassword(userId: String, currentPassword: String, newPassword: String): Mono<Unit> =
        ReactiveWebContext.getCurrentUser()
            .validate {
                isTrue(passwordEncoder.matches(currentPassword, it.password)) { "Current password is incorrect" }
                isFalse(passwordEncoder.matches(newPassword, it.password)) { "Current password is the same as the previous password" }
            }
            .flatMap { commandGateway.send(SetUserPasswordCommand(userId, passwordEncoder.encode(newPassword))) }
}
