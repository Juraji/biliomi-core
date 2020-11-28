package nl.juraji.biliomi.api.users

import nl.juraji.biliomi.security.UserPrincipal
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/users")
@PreAuthorize("hasRole('ROLE_ADMIN')")
class UsersController(
        private val usersService: UsersService,
) {

    @GetMapping
    fun findUsers(): Flux<UserPrincipal> = usersService.findUsers()

    @GetMapping("{userId}")
    fun findUser(
            @PathVariable("userId") userId: String,
    ): Mono<UserPrincipal> = usersService.findUser(userId)

    @PostMapping
    fun createUser(
            @RequestBody userDto: CreateUserDto,
    ): Mono<UserPrincipal> = usersService.createUser(
            userDto.username,
            userDto.password,
            userDto.roles
    )
}
