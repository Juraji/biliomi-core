package nl.juraji.biliomi.api.users

import nl.juraji.biliomi.configuration.security.Authorities
import nl.juraji.biliomi.domain.user.commands.CreateAuthorityGroupCommand
import nl.juraji.biliomi.domain.user.commands.DeleteAuthorityGroupCommand
import nl.juraji.biliomi.domain.user.commands.UpdateAuthorityGroupCommand
import nl.juraji.biliomi.projections.AuthorityGroupProjection
import nl.juraji.biliomi.projections.repositories.AuthorityGroupProjectionRepository
import nl.juraji.biliomi.utils.extensions.uuid
import nl.juraji.reactor.validations.validateAsync
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class AuthorityGroupsService(
    private val authorityGroupRepository: AuthorityGroupProjectionRepository,
    private val commandGateway: ReactorCommandGateway
) {

    fun getAuthorityGroups(): Flux<AuthorityGroupProjection> = authorityGroupRepository.findAll()

    fun createAuthorityGroup(groupName: String, authorities: Set<String>): Mono<String> =
        validateAsync { isFalse(authorityGroupRepository.existsByName(groupName)) { "A group with name $groupName already exists" } }
            .flatMap {
                commandGateway.send(
                    CreateAuthorityGroupCommand(
                        groupId = uuid(),
                        groupName = groupName,
                        authorities = authorities
                    )
                )
            }

    fun copyAuthorityGroup(sourceGroupId: String, newGroupName: String): Mono<String> =
        authorityGroupRepository
            .findById(sourceGroupId)
            .validateAsync { isFalse(authorityGroupRepository.existsByName(newGroupName)) { "A group with name $newGroupName already exists" } }
            .flatMap {
                commandGateway.send(
                    CreateAuthorityGroupCommand(
                        groupId = uuid(),
                        groupName = newGroupName,
                        authorities = it.authorities
                    )
                )
            }

    fun updateAuthorityGroup(update: AuthorityGroupProjection): Mono<Unit> = authorityGroupRepository
        .findById(update.groupId)
        .validateAsync {
            unless(it.groupName == update.groupName) {
                isFalse(authorityGroupRepository.existsByName(update.groupName)) { "A group with name ${update.groupName} already exists" }
            }
        }
        .flatMap {
            commandGateway.send(
                UpdateAuthorityGroupCommand(
                    groupId = it.groupId,
                    groupName = update.groupName,
                    authorities = update.authorities,
                    default = update.default
                )
            )
        }

    fun deleteAuthorityGroup(groupId: String): Mono<Unit> = commandGateway.send(DeleteAuthorityGroupCommand(groupId))

    fun getPermissionList(): Flux<GrantedAuthority> = Flux
        .fromIterable(Authorities.all)
        .map { GrantedAuthority { it } }
}
