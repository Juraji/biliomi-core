package nl.juraji.biliomi.api.users

import nl.juraji.biliomi.configuration.security.Authorities
import nl.juraji.biliomi.security.AuthorityGroup
import nl.juraji.biliomi.security.repositories.AuthorityGroupRepository
import nl.juraji.biliomi.utils.ValidateAsync
import nl.juraji.biliomi.utils.extensions.uuid
import nl.juraji.biliomi.utils.validate
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class AuthorityGroupsService(
        private val authorityGroupRepository: AuthorityGroupRepository,
) {

    fun getAuthorityGroups(): Flux<AuthorityGroup> = authorityGroupRepository.findAll()

    fun createAuthorityGroup(groupName: String, authorities: Set<String>): Mono<AuthorityGroup> = ValidateAsync
            .all {
                isNotBlank(groupName) { "Group name should not be blank" }
                isNotEmpty(authorities) { "Group authorities may be empty" }
                isTrue(authorities.all(String::isNotBlank)) { "Group authorities may not contain empty values" }
                isTrue(authorities.all(Authorities.all::contains)) { "Unknown entry found in authorities" }
            }
            .map {
                AuthorityGroup(
                        groupId = uuid(),
                        name = groupName,
                        authorities = authorities
                )
            }
            .flatMap(authorityGroupRepository::save)

    fun updateAuthorityGroup(authorityGroup: AuthorityGroup): Mono<AuthorityGroup> = ValidateAsync
            .all {
                isNotBlank(authorityGroup.name) { "Group name should not be blank" }
                isNotEmpty(authorityGroup.authorities) { "Group authorities may be empty" }
                isTrue(authorityGroup.authorities.all(String::isNotBlank)) { "Group authorities may not contain empty values" }
                isTrue(authorityGroup.authorities.all(Authorities.all::contains)) { "Unknown entry found in authorities" }
            }
            .flatMap {
                authorityGroupRepository.update(authorityGroup.groupId) {
                    copy(
                            name = authorityGroup.name,
                            authorities = authorityGroup.authorities
                    )
                }
            }

    fun deleteAuthorityGroup(authorityGroupId: String): Mono<AuthorityGroup> = authorityGroupRepository
            .findById(authorityGroupId)
            .validate { isFalse(it.protected) { "Authority group ${it.name} is protected and can not be deleted" } }
            .flatMap { authorityGroupRepository.deleteById(authorityGroupId) }

    fun getPermissionList(): Flux<GrantedAuthority> = Flux
            .fromIterable(Authorities.all)
            .map { GrantedAuthority { it } }
}
