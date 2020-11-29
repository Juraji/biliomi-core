package nl.juraji.biliomi.api.users

import nl.juraji.biliomi.configuration.security.Authorities
import nl.juraji.biliomi.security.AuthorityGroup
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
    fun getAuthorityGroups(): Flux<AuthorityGroup> = authorityGroupsService.getAuthorityGroups()

    @PostMapping
    @PreAuthorize("hasRole('${Authorities.GROUPS_CREATE}')")
    fun createAuthorityGroup(
            @RequestBody createAuthorityGroupDto: CreateAuthorityGroupDto,
    ): Mono<AuthorityGroup> = authorityGroupsService.createAuthorityGroup(
            groupName = createAuthorityGroupDto.groupName,
            authorities = createAuthorityGroupDto.authorities,
    )

    @PutMapping
    @PreAuthorize("hasRole('${Authorities.GROUPS_UPDATE}')")
    fun updateAuthorityGroup(
            @RequestBody authorityGroup: AuthorityGroup,
    ): Mono<AuthorityGroup> = authorityGroupsService.updateAuthorityGroup(authorityGroup)

    @PreAuthorize("hasRole('${Authorities.GROUPS_DELETE}')")
    @DeleteMapping("/{authorityGroupId}")
    fun deleteAuthorityGroup(
            @PathVariable("authorityGroupId") authorityGroupId: String
    ): Mono<AuthorityGroup> = authorityGroupsService.deleteAuthorityGroup(authorityGroupId)

    @GetMapping("/permission-list")
    fun getPermissionList(): Flux<GrantedAuthority> = authorityGroupsService.getPermissionList()
}
