package nl.juraji.biliomi.api.users

import nl.juraji.biliomi.domain.user.UserId
import nl.juraji.biliomi.projections.UserProjection
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/users")
class UsersController(
        private val usersService: UsersService,
) {

    @GetMapping
    fun findUsers(): Flux<UserProjection> = usersService.findUsers()

    @GetMapping("{userId}")
    fun findUser(
            @PathVariable("userId") userId: UserId,
    ): Mono<UserProjection> = usersService.findUser(userId)

    @PostMapping
    fun createUser(
            @RequestBody user: CreateUserDTO,
    ): Mono<CreateUserDTO> = usersService
            .createUser(user.username)
            .map { userId -> user.copy(userId = userId.identifier) }

    @PostMapping("/{userId}/points")
    fun addPoints(
            @PathVariable("userId") userId: UserId,
            @RequestParam("amount") amount: Long,
    ): Mono<PointBalanceDTO> = when {
        amount > 0 -> usersService.addPoints(userId, amount)
        else -> usersService.subtractPoints(userId, -amount)
    }.map { newBalance -> PointBalanceDTO(userId = userId.identifier, pointBalance = newBalance) }
}
