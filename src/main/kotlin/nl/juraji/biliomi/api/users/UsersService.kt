package nl.juraji.biliomi.api.users

import nl.juraji.biliomi.security.UserDetailsManager
import nl.juraji.biliomi.security.UserPrincipal
import nl.juraji.biliomi.security.repositories.UserPrincipalRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class UsersService(
        private val userPrincipalRepository: UserPrincipalRepository,
        private val userDetailsManager: UserDetailsManager,
) {

    fun findUsers(): Flux<UserPrincipal> = userPrincipalRepository
            .findAll()
            .map(UserPrincipal::eraseCredentialsK)

    fun findUser(userId: String): Mono<UserPrincipal> = userPrincipalRepository
            .findById(userId)
            .map(UserPrincipal::eraseCredentialsK)

    fun createUser(username: String, password: String): Mono<UserPrincipal> = userDetailsManager
            .createUser(
                    username = username,
                    password = password,
            )
            .toMono()
}
