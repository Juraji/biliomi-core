package nl.juraji.biliomi.configuration.axon

import nl.juraji.biliomi.utils.withWebContext
import org.axonframework.commandhandling.CommandMessage
import org.axonframework.commandhandling.GenericCommandMessage
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.userdetails.User
import reactor.kotlin.core.publisher.toMono
import reactor.test.StepVerifier

internal class UserPrincipalCommandInterceptorTest {

    private val interceptor = UserPrincipalCommandInterceptor()

    @Test
    internal fun `should set current user authority info as command metadata`() {
        val commandMessage: CommandMessage<*> = GenericCommandMessage("Some payload")
        val user = User.builder()
            .username("User")
            .password("")
            .authorities("ROLE_X")
            .build()

        StepVerifier.create(withWebContext(
            authentication = TestingAuthenticationToken(user, null)
        ) { interceptor.intercept(commandMessage.toMono()) })
            .expectNextMatches {
                it.metaData[UserPrincipalCommandInterceptor.USERNAME] == "User"
                        && it.metaData[UserPrincipalCommandInterceptor.ACCOUNT_ENABLED] == true
                        && it.metaData[UserPrincipalCommandInterceptor.AUTHORITIES] == listOf("ROLE_X")
            }
            .verifyComplete()

    }

    @Test
    internal fun `should set SYSTEM user authority info as command metadata if security context is empty`() {
        val commandMessage: CommandMessage<*> = GenericCommandMessage("Some payload")

        StepVerifier.create(interceptor.intercept(commandMessage.toMono()))
            .expectNextMatches {
                it.metaData[UserPrincipalCommandInterceptor.USERNAME] == "SYSTEM"
                        && it.metaData[UserPrincipalCommandInterceptor.ACCOUNT_ENABLED] == false
                        && it.metaData[UserPrincipalCommandInterceptor.AUTHORITIES] == emptyList<String>()
            }
            .verifyComplete()
    }
}
