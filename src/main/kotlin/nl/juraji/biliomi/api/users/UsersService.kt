package nl.juraji.biliomi.api.users

import nl.juraji.biliomi.domain.user.UserId
import nl.juraji.biliomi.domain.user.commands.AddPointsCommand
import nl.juraji.biliomi.domain.user.commands.CreateUserCommand
import nl.juraji.biliomi.domain.user.commands.SubtractPointsCommand
import nl.juraji.biliomi.projections.UserProjection
import nl.juraji.biliomi.projections.repositories.UserRepository
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class UsersService(
        private val commandGateway: ReactorCommandGateway,
        private val userRepository: UserRepository,
) {

    fun findUsers(): Flux<UserProjection> = userRepository
            .findAll()

    fun findUser(userId: UserId): Mono<UserProjection> = userRepository
            .findById(userId.identifier)

    fun createUser(username: String): Mono<UserId> = commandGateway
            .send(CreateUserCommand(userId = UserId(), username = username))

    fun addPoints(userId: UserId, amount: Long): Mono<Long> = commandGateway
            .send(AddPointsCommand(userId = userId, amount = amount))

    fun subtractPoints(userId: UserId, amount: Long): Mono<Long> = commandGateway
            .send(SubtractPointsCommand(userId = userId, amount = amount))
}
