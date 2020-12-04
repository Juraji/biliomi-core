package nl.juraji.biliomi.api

import nl.juraji.biliomi.projections.UserPrincipal
import org.springframework.security.core.context.SecurityContext
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

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
        .switchIfEmpty {
            UserPrincipal(
                userId = "SYSTEM",
                authorities = emptyList(),
                username = "System",
                password = "",
                enabled = true,
            ).toMono()
        }

}
