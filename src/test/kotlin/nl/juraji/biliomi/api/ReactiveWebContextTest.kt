package nl.juraji.biliomi.api

import nl.juraji.biliomi.utils.withWebContext
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.userdetails.User
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

    @Test
    internal fun `should return current user`() {
        StepVerifier.create(withWebContext(
            authentication = TestingAuthenticationToken(
                User.builder()
                    .username("User")
                    .password("")
                    .authorities("ROLE_X")
                    .build(),
                null
            )
        ) { ReactiveWebContext.getCurrentUser() })
            .expectNextCount(1)
            .verifyComplete()
    }

    @Test
    internal fun `should fail when no current user is set`() {
        StepVerifier.create(ReactiveWebContext.getCurrentUser())
            .expectError(AuthenticationCredentialsNotFoundException::class.java)
            .verify()
    }
}
