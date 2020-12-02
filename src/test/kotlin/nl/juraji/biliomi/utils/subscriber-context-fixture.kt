package nl.juraji.biliomi.utils

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

fun <T> withWebContext(
    method: HttpMethod = HttpMethod.POST,
    url: String = "/api/test",
    headers: HttpHeaders.() -> Unit = {},
    queryParams: HttpHeaders.() -> Unit = {},
    authentication: Authentication = TestingAuthenticationToken(null, null),
    supplier: () -> Mono<T>,
): Mono<T> = supplier().contextWrite { ctx ->
    ctx
        .put(
            ServerWebExchange::class.java, MockServerWebExchange.from(
                MockServerHttpRequest
                    .method(method, url)
                    .headers(HttpHeaders().apply(headers))
                    .queryParams(HttpHeaders().apply(queryParams))
            )
        )
        .put(SecurityContext::class.java, Mono.just(SecurityContextImpl(authentication)))
}

fun <T> withWebContextFlux(
    method: HttpMethod = HttpMethod.POST,
    url: String = "/api/test",
    headers: HttpHeaders.() -> Unit = {},
    queryParams: HttpHeaders.() -> Unit = {},
    authentication: Authentication = TestingAuthenticationToken(null, null),
    supplier: () -> Flux<T>,
): Flux<T> = supplier().contextWrite { ctx ->
    ctx
        .put(
            ServerWebExchange::class.java, MockServerWebExchange.from(
                MockServerHttpRequest
                    .method(method, url)
                    .headers(HttpHeaders().apply(headers))
                    .queryParams(HttpHeaders().apply(queryParams))
            )
        )
        .put(SecurityContext::class.java, Mono.just(SecurityContextImpl(authentication)))
}
