package nl.juraji.biliomi.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import nl.juraji.biliomi.security.AuthorityGroup
import nl.juraji.biliomi.security.UserPrincipal
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContext
import org.springframework.security.test.context.support.WithSecurityContextFactory

@Configuration
@Profile("test")
class ApiTestConfiguration {

    @Bean("objectMapper")
    fun objectMapper(
        objectMapperBuilder: Jackson2ObjectMapperBuilder,
    ): ObjectMapper = objectMapperBuilder.build()
}

@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = WithMockPrincipalSecurityContextFactory::class)
annotation class WithMockPrincipal(
    val userId: String = "user##",
    val username: String = "Username",
    val roles: Array<String> = []
)

class WithMockPrincipalSecurityContextFactory : WithSecurityContextFactory<WithMockPrincipal> {
    override fun createSecurityContext(annotation: WithMockPrincipal): SecurityContext {
        val context = SecurityContextHolder.createEmptyContext()
        val authorityGroups: Set<AuthorityGroup> =
            if (annotation.roles.isNotEmpty()) setOf(
                AuthorityGroup(
                    groupId = "group##",
                    name = "Mock Authority Group",
                    protected = false,
                    authorities = annotation.roles.toSet()
                )
            )
            else emptySet()


        val principal = UserPrincipal(
            userId = annotation.userId,
            username = annotation.username,
            password = "",
            authorityGroups = authorityGroups
        )

        context.authentication = UsernamePasswordAuthenticationToken(principal, null, principal.authorities)

        return context
    }
}
