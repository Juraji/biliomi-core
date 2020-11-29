package nl.juraji.biliomi.api.users

import nl.juraji.biliomi.configuration.security.Authorities
import nl.juraji.biliomi.security.UserPrincipal
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/users")
class UsersController(
        private val usersService: UsersService,
) {

    @GetMapping("/me")
    @PreAuthorize("hasRole('${Authorities.USERS_READ_ME}')")
    fun getMyUser(
            @AuthenticationPrincipal principal: UserPrincipal
    ): Mono<UserPrincipal> = usersService.findUser(principal.userId)

    @PutMapping("/me/username")
    @PreAuthorize("hasRole('${Authorities.USERS_UPDATE_ME_USERNAME}')")
    fun updateMyUsername(
            @AuthenticationPrincipal principal: UserPrincipal,
            @RequestParam("newUsername") newUsername: String
    ): Mono<UserPrincipal> = usersService.updateUsername(principal.userId, newUsername)

    @PutMapping("/me/password")
    @PreAuthorize("hasRole('${Authorities.USERS_UPDATE_ME_PASSWORD}')")
    fun updateMyPassword(
            @AuthenticationPrincipal principal: UserPrincipal,
            @RequestParam("oldPassword") oldPassword: String,
            @RequestParam("newPassword") newPassword: String,
    ): Mono<UserPrincipal> = usersService.updatePassword(principal.userId, oldPassword, newPassword)

    @GetMapping
    @PreAuthorize("hasRole('${Authorities.USERS_READ_ALL}')")
    fun findUsers(): Flux<UserPrincipal> = usersService.findUsers()

    @GetMapping("{userId}")
    @PreAuthorize("hasRole('${Authorities.USERS_READ_ALL}')")
    fun findUser(
            @PathVariable("userId") userId: String,
    ): Mono<UserPrincipal> = usersService.findUser(userId)

    @PostMapping
    @PreAuthorize("hasRole('${Authorities.USERS_CREATE}')")
    fun createUser(
            @RequestBody userDto: MutateUserDto,
    ): Mono<UserPrincipal> = usersService.createUser(userDto.username, userDto.password)

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('${Authorities.USERS_DELETE}') AND #userId != principal.userId")
    fun deleteUser(
            @PathVariable("userId") userId: String
    ): Mono<UserPrincipal> = usersService.deleteUser(userId)

    @PostMapping("/{userId}/groups")
    @PreAuthorize("hasRole('${Authorities.USERS_ADD_GROUP}')")
    fun addGroupToUser(
            @PathVariable("userId") userId: String,
            @RequestParam("groupId") groupId: String
    ): Mono<UserPrincipal> = usersService.addGroupToUser(userId, groupId)

    @DeleteMapping("/{userId}/groups/{groupId}")
    @PreAuthorize("hasRole('${Authorities.USERS_REMOVE_GROUP}')")
    fun removeGroupFromUser(
            @PathVariable("userId") userId: String,
            @PathVariable("groupId") groupId: String,
    ): Mono<UserPrincipal> = usersService.removeGroupFromUser(userId, groupId)
}
