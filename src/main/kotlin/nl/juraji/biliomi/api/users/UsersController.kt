package nl.juraji.biliomi.api.users

import nl.juraji.biliomi.configuration.security.Authorities
import nl.juraji.biliomi.projections.UserProjection
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
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
        @AuthenticationPrincipal principal: UserDetails
    ): Mono<UserProjection> = usersService.findUser(principal.username)

    @PutMapping("/me/display-name")
    @PreAuthorize("hasRole('${Authorities.USERS_UPDATE_ME_DISPLAY_NAME}')")
    fun updateMyUsername(
        @AuthenticationPrincipal principal: UserDetails,
        @RequestParam("newDisplayName") newDisplayName: String
    ): Mono<Unit> = usersService.updateDisplayName(principal.username, newDisplayName)

    @PutMapping("/me/password")
    @PreAuthorize("hasRole('${Authorities.USERS_UPDATE_ME_PASSWORD}')")
    fun updateMyPassword(
        @AuthenticationPrincipal principal: UserDetails,
        @RequestParam("oldPassword") oldPassword: String,
        @RequestParam("newPassword") newPassword: String,
    ): Mono<Unit> = usersService.updatePassword(principal.username, oldPassword, newPassword)

    @GetMapping
    @PreAuthorize("hasRole('${Authorities.USERS_READ_ALL}')")
    fun findUsers(): Flux<UserProjection> = usersService.findUsers()

    @GetMapping("{username}")
    @PreAuthorize("hasRole('${Authorities.USERS_READ_ALL}')")
    fun findUser(
        @PathVariable("username") username: String,
    ): Mono<UserProjection> = usersService.findUser(username)

    @PostMapping
    @PreAuthorize("hasRole('${Authorities.USERS_CREATE}')")
    fun createUser(
        @RequestBody userDto: CreateUserDto,
    ): Mono<UserCreatedDTO> = usersService
        .createUser(userDto.username, userDto.displayName, userDto.password)
        .map { UserCreatedDTO(it) }

    @DeleteMapping("/{username}")
    @PreAuthorize("hasRole('${Authorities.USERS_DELETE}') AND #username != principal.username")
    fun deleteUser(
        @PathVariable("username") username: String
    ): Mono<Unit> = usersService.deleteUser(username)

    @PostMapping("/{username}/groups")
    @PreAuthorize("hasRole('${Authorities.USERS_ADD_GROUP}')")
    fun addGroupToUser(
        @PathVariable("username") username: String,
        @RequestParam("groupId") groupId: String
    ): Mono<Unit> = usersService.addGroupToUser(username, groupId)

    @DeleteMapping("/{username}/groups/{groupId}")
    @PreAuthorize("hasRole('${Authorities.USERS_REMOVE_GROUP}')")
    fun removeGroupFromUser(
        @PathVariable("username") username: String,
        @PathVariable("groupId") groupId: String,
    ): Mono<Unit> = usersService.removeGroupFromUser(username, groupId)
}
