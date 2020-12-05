package nl.juraji.biliomi.configuration.axon

import nl.juraji.biliomi.api.ReactiveWebContext
import org.axonframework.commandhandling.CommandMessage
import org.axonframework.extensions.reactor.messaging.ReactorMessageDispatchInterceptor
import org.springframework.security.core.userdetails.User
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

class UserPrincipalCommandInterceptor : ReactorMessageDispatchInterceptor<CommandMessage<*>> {
    override fun intercept(message: Mono<CommandMessage<*>>): Mono<CommandMessage<*>> {
        val user = ReactiveWebContext
            .getCurrentUser()
            .onErrorResume {
                User
                    .withUsername("SYSTEM")
                    .password("")
                    .authorities(emptyList())
                    .disabled(true)
                    .build()
                    .toMono()
            }

        return Mono
            .zip(message, user) { cmd, principal ->
                cmd.andMetaData(
                    mapOf(
                        USERNAME to principal.username,
                        ACCOUNT_ENABLED to principal.isEnabled,
                        AUTHORITIES to principal.authorities.map { it.authority }
                    )
                )
            }
    }

    companion object {
        const val USERNAME = "principal_username"
        const val ACCOUNT_ENABLED = "principal_account_enabled"
        const val AUTHORITIES = "principal_authorities"
    }
}
