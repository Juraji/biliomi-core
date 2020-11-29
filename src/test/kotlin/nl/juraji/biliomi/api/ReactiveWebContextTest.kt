package nl.juraji.biliomi.api

import nl.juraji.biliomi.utils.withWebContext
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier

internal class ReactiveWebContextTest {

    @Test
    fun `should return SecurityContext when present`() {
        StepVerifier.create(withWebContext { ReactiveWebContext.getSecurityContext() })
                .expectNextCount(1)
                .verifyComplete()
    }

    @Test
    fun `should return empty mono when SecurityContext not present`() {
        StepVerifier.create(ReactiveWebContext.getSecurityContext())
                .verifyComplete()
    }

    @Test
    fun `should return ServerWebExchange when present`() {
        StepVerifier.create(withWebContext { ReactiveWebContext.getServerWebExchange() })
                .expectNextCount(1)
                .verifyComplete()
    }

    @Test
    fun `should return empty mono when ServerWebExchange not present`() {
        StepVerifier.create(ReactiveWebContext.getServerWebExchange())
                .verifyComplete()
    }
}
