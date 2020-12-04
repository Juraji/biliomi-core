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
        @RequestBody createAuthorityGroupDto: CreateAuthorityGroupDto,
    ): Mono<AuthorityGroupCreatedDTO> = authorityGroupsService
        .createAuthorityGroup(
            groupName = createAuthorityGroupDto.groupName,
            authorities = createAuthorityGroupDto.authorities,
        )
        .map { AuthorityGroupCreatedDTO(it) }

    @PostMapping("/copy")
    @PreAuthorize("hasRole('${Authorities.GROUPS_CREATE}')")
    fun copyAuthorityGroup(
        @RequestParam("sourceGroupId") sourceGroupId: String,
        @RequestParam("newGroupName") newGroupName: String
    ): Mono<AuthorityGroupCreatedDTO> = authorityGroupsService
        .copyAuthorityGroup(sourceGroupId, newGroupName)
        .map { AuthorityGroupCreatedDTO(it) }

    @PutMapping
    @PreAuthorize("hasRole('${Authorities.GROUPS_UPDATE}')")
    fun updateAuthorityGroup(
        @RequestBody authorityGroup: AuthorityGroupProjection,
    ): Mono<Unit> = authorityGroupsService.updateAuthorityGroup(authorityGroup)

    @PreAuthorize("hasRole('${Authorities.GROUPS_DELETE}')")
    @DeleteMapping("/{authorityGroupId}")
    fun deleteAuthorityGroup(
        @PathVariable("authorityGroupId") authorityGroupId: String
    ): Mono<Unit> = authorityGroupsService.deleteAuthorityGroup(authorityGroupId)

    @GetMapping("/permission-list")
    fun getPermissionList(): Flux<GrantedAuthority> = authorityGroupsService.getPermissionList()
}
