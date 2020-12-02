package nl.juraji.biliomi.api.users

import nl.juraji.biliomi.configuration.security.Authorities
import nl.juraji.biliomi.security.AuthorityGroup
import nl.juraji.biliomi.security.repositories.AuthorityGroupRepository
import nl.juraji.biliomi.utils.extensions.uuid
import nl.juraji.reactor.validations.validate
import nl.juraji.reactor.validations.validateAsync
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class AuthorityGroupsService(
    private val authorityGroupRepository: AuthorityGroupRepository,
) {

    fun getAuthorityGroups(): Flux<AuthorityGroup> = authorityGroupRepository.findAll()

    fun createAuthorityGroup(groupName: String, authorities: Set<String>): Mono<AuthorityGroup> =
        validateAsync {
            isFalse(authorityGroupRepository.existsByName(groupName)) { "A group with name $groupName already exists" }

            synchronous {
                isNotBlank(groupName) { "Group name should not be blank" }
                isNotEmpty(authorities) { "Group authorities may be empty" }
                isTrue(authorities.all(String::isNotBlank)) { "Group authorities may not contain empty values" }
                isTrue(authorities.all(Authorities.all::contains)) { "Unknown entry found in authorities" }
            }
        }
            .map {
                AuthorityGroup(
                    groupId = uuid(),
                    name = groupName,
                    authorities = authorities
                )
            }
            .flatMap(authorityGroupRepository::save)

    fun copyAuthorityGroup(sourceGroupId: String, newGroupName: String): Mono<AuthorityGroup> = authorityGroupRepository
        .findById(sourceGroupId)
        .validateAsync {
            isFalse(authorityGroupRepository.existsByName(newGroupName)) { "A group with name $newGroupName already exists" }
            synchronous {
                isNotBlank(newGroupName) { "Group name should not be blank" }
            }
        }
        .map {
            it.copy(
                groupId = uuid(),
                name = newGroupName
            )
        }
        .flatMap(authorityGroupRepository::save)

    fun updateAuthorityGroup(update: AuthorityGroup): Mono<AuthorityGroup> = authorityGroupRepository
        .findById(update.groupId)
        .validateAsync {
            unless(it.name == update.name) {
                isFalse(authorityGroupRepository.existsByName(update.name)) { "A group with name ${update.name} already exists" }
            }

            synchronous {
                isNotBlank(update.name) { "Group name should not be blank" }
                isNotEmpty(update.authorities) { "Group authorities may be empty" }
                isTrue(update.authorities.all(String::isNotBlank)) { "Group authorities may not contain empty values" }
                isTrue(update.authorities.all(Authorities.all::contains)) { "Unknown entry found in authorities" }
            }
        }
        .map {
            it.copy(
                name = update.name,
                authorities = update.authorities
            )
        }
        .flatMap(authorityGroupRepository::save)

    fun deleteAuthorityGroup(authorityGroupId: String): Mono<AuthorityGroup> = authorityGroupRepository
        .findById(authorityGroupId)
        .validate { isFalse(it.protected) { "Authority group ${it.name} is protected and can not be deleted" } }
        .flatMap { authorityGroupRepository.deleteById(authorityGroupId) }

    fun getPermissionList(): Flux<GrantedAuthority> = Flux
        .fromIterable(Authorities.all)
        .map { GrantedAuthority { it } }
}
