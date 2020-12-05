package nl.juraji.biliomi.api.users

import nl.juraji.biliomi.api.ReactiveWebContext
import nl.juraji.biliomi.domain.user.commands.*
import nl.juraji.biliomi.projections.UserProjection
import nl.juraji.biliomi.projections.repositories.AuthorityGroupProjectionRepository
import nl.juraji.biliomi.projections.repositories.UserProjectionRepository
import nl.juraji.reactor.validations.ValidationException
import nl.juraji.reactor.validations.validate
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

    fun findUser(username: String): Mono<UserProjection> = userRepository.findById(username)

    fun createUser(username: String, displayName: String?, password: String?): Mono<String> =
        commandGateway.send(CreateUserCommand(
            username = username,
            displayName = displayName ?: username,
            passwordHash = password?.let { passwordEncoder.encode(it) }
        ))

    fun deleteUser(username: String): Mono<Unit> = commandGateway.send(DeleteUserCommand(username))

    fun addGroupToUser(username: String, groupId: String): Mono<Unit> = authorityGroupRepository
        .findById(groupId)
        .onErrorMap { ValidationException("Authority group with id $groupId does not exist") }
        .flatMap { commandGateway.send(AddUserToAuthorityGroupCommand(username, groupId)) }

    fun removeGroupFromUser(username: String, groupId: String): Mono<Unit> =
        commandGateway.send(RemoveUserFromAuthorityGroupCommand(username, groupId))

    fun updateDisplayName(username: String, newDisplayName: String): Mono<Unit> =
        commandGateway.send(SetUserDisplayNameCommand(username, newDisplayName))

    fun updatePassword(username: String, currentPassword: String, newPassword: String): Mono<Unit> =
        ReactiveWebContext.getCurrentUser()
            .validate {
                isTrue(passwordEncoder.matches(currentPassword, it.password)) { "Current password is incorrect" }
                isFalse(passwordEncoder.matches(newPassword, it.password)) {
                    "Current password is the same as the previous password"
                }
            }
            .flatMap { commandGateway.send(SetUserPasswordCommand(username, passwordEncoder.encode(newPassword))) }
}
