package nl.juraji.biliomi.configuration

import nl.juraji.biliomi.security.UserDetailsManager
import nl.juraji.biliomi.utils.LoggerCompanion
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import reactor.core.publisher.Mono
import java.util.*

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfiguration {

    @Bean
    fun passwordEncoder(): PasswordEncoder = Pbkdf2PasswordEncoder()

    @Bean
    fun userDetailsService(
            userDetailsManager: UserDetailsManager,
    ): ReactiveUserDetailsService = ReactiveUserDetailsService { username ->
        Mono.justOrEmpty(userDetailsManager.loadUserByUsername(username))
    }

    @Bean
    fun securityWebFilterChain(
            http: ServerHttpSecurity,
    ): SecurityWebFilterChain = http
            .authorizeExchange()
            .pathMatchers("/actuator/**").hasAuthority("ROLE_ADMIN")
            .pathMatchers("/events").permitAll()
            .anyExchange().authenticated()
            .and().httpBasic()
            .and().build()
}

@Configuration
class InitialUsersConfiguration(
        private val userDetailsManager: UserDetailsManager,
) {

    @EventListener(ContextRefreshedEvent::class)
    fun initialUser() = userDetailsManager.run {
        if (!userExists(ADMIN_USERNAME)) {
            val password = UUID.randomUUID().toString()

            createUser(
                    username = ADMIN_USERNAME,
                    password = password,
                    authorities = ADMIN_AUTHORITIES
            )

            logger.info("""
                
                Created initial admin user:
                Username: $ADMIN_USERNAME
                Password: $password
                
                Make sure to save this to a safe place, since it will not be recoverable when lost!
                
            """.trimIndent())
        }
    }

    companion object : LoggerCompanion(InitialUsersConfiguration::class) {
        val ADMIN_USERNAME: String by lazy { "admin" }
        val ADMIN_AUTHORITIES: Set<GrantedAuthority> by lazy { setOf(GrantedAuthority { "ROLE_ADMIN" }) }
    }
}

