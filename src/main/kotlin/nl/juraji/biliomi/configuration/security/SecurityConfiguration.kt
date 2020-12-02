package nl.juraji.biliomi.configuration.security

import nl.juraji.biliomi.security.repositories.UserPrincipalRepository
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfiguration {

    @Bean
    fun passwordEncoder(): PasswordEncoder = Pbkdf2PasswordEncoder()

    @Bean
    fun userDetailsService(
        userPrincipalRepository: UserPrincipalRepository
    ): ReactiveUserDetailsService = ReactiveUserDetailsService { username ->
        userPrincipalRepository.findByUsername(username)
            .map { it as UserDetails }
            .switchIfEmpty { Mono.error(UsernameNotFoundException("User $username was not found")) }
    }

    @Bean
    fun securityWebFilterChain(
        http: ServerHttpSecurity,
    ): SecurityWebFilterChain = http
        .csrf().disable()
        .authorizeExchange()
        .pathMatchers("/actuator/**").hasAuthority("ROLE_ADMIN")
        .anyExchange().authenticated()
        .and().httpBasic()
        .and().build()
}
