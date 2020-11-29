package nl.juraji.biliomi.configuration.axon

import nl.juraji.biliomi.api.ReactiveWebContext
import org.axonframework.commandhandling.CommandMessage
import org.axonframework.extensions.reactor.messaging.ReactorMessageDispatchInterceptor
import reactor.core.publisher.Mono

class UserPrincipalCommandInterceptor: ReactorMessageDispatchInterceptor<CommandMessage<*>> {
    override fun intercept(message: Mono<CommandMessage<*>>): Mono<CommandMessage<*>> = Mono
            .zip(message, ReactiveWebContext.getCurrentUser()) { cmd, principal ->
                cmd.andMetaData(mapOf(
                        USER_ID to principal.userId,
                        USERNAME to principal.username,
                ))
            }

    companion object{
        const val USER_ID = "principal_user_id"
        const val USERNAME = "principal_username"
    }
}
