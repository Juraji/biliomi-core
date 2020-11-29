package nl.juraji.biliomi.api

import nl.juraji.biliomi.security.UserPrincipal
import org.springframework.security.core.context.SecurityContext
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

object ReactiveWebContext {

    fun getSecurityContext(): Mono<SecurityContext> =
            Mono.deferContextual { Mono.just(it) }
                    .filter { it.hasKey(SecurityContext::class.java) }
                    .flatMap { it.get<Mono<SecurityContext>>(SecurityContext::class.java) }

    fun getServerWebExchange(): Mono<ServerWebExchange> =
            Mono.deferContextual { Mono.just(it) }
                    .filter { it.hasKey(ServerWebExchange::class.java) }
                    .map { it.get(ServerWebExchange::class.java) }

    fun getCurrentUser(): Mono<UserPrincipal> = getSecurityContext()
            .map { it.authentication.principal as UserPrincipal }

}
