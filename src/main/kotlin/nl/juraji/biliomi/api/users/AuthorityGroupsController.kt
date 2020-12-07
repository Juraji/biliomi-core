package nl.juraji.biliomi.api.users

import nl.juraji.biliomi.configuration.security.Authorities
import nl.juraji.biliomi.projections.AuthorityGroupProjection
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.GrantedAuthority
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/groups")
class AuthorityGroupsController(
    private val authorityGroupsService: AuthorityGroupsService,
) {

    @GetMapping
    @PreAuthorize("hasRole('${Authorities.GROUPS_READ_ALL}')")
    fun getAuthorityGroups(): Flux<AuthorityGroupProjection> = authorityGroupsService.getAuthorityGroups()

    @PostMapping
    @PreAuthorize("hasRole('${Authorities.GROUPS_CREATE}')")
    fun createAuthorityGroup(
        @RequestParam("name") name: String,
        @RequestParam("authorities") authorities: Set<String>,
    ): Mono<AuthorityGroupCreatedDTO> = authorityGroupsService
        .createAuthorityGroup(name, authorities)
        .map { AuthorityGroupCreatedDTO(it) }

    @PostMapping("/{groupId}/copy")
    @PreAuthorize("hasRole('${Authorities.GROUPS_CREATE}')")
    fun copyAuthorityGroup(
        @PathVariable("groupId") groupId: String,
        @RequestParam("name") name: String
    ): Mono<AuthorityGroupCreatedDTO> = authorityGroupsService
        .copyAuthorityGroup(groupId, name)
        .map { AuthorityGroupCreatedDTO(it) }

    @PutMapping("/{groupId}")
    @PreAuthorize("hasRole('${Authorities.GROUPS_UPDATE}')")
    fun updateAuthorityGroup(
        @PathVariable("groupId") groupId: String,
        @RequestBody authorityGroup: AuthorityGroupProjection,
    ): Mono<Unit> = authorityGroupsService.updateAuthorityGroup(groupId, authorityGroup)

    @PreAuthorize("hasRole('${Authorities.GROUPS_DELETE}')")
    @DeleteMapping("/{groupId}")
    fun deleteAuthorityGroup(
        @PathVariable("groupId") groupId: String
    ): Mono<Unit> = authorityGroupsService.deleteAuthorityGroup(groupId)

    @GetMapping("/permission-list")
    fun getPermissionList(): Flux<GrantedAuthority> = authorityGroupsService.getPermissionList()
}
