package nl.juraji.biliomi.api.bank

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import nl.juraji.biliomi.api.ReactiveWebContext
import nl.juraji.biliomi.configuration.ApiTestConfiguration
import nl.juraji.biliomi.configuration.WithMockPrincipal
import nl.juraji.biliomi.configuration.security.Authorities
import nl.juraji.biliomi.projections.BankProjection
import nl.juraji.biliomi.utils.extensions.uuid
import nl.juraji.biliomi.utils.returnsMonoOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@Import(ApiTestConfiguration::class)
@WebFluxTest(BankAccountController::class)
@AutoConfigureWebTestClient
internal class BankAccountControllerTest {

    @MockkBean
    private lateinit var bankAccountService: BankAccountService

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    @Qualifier("objectMapper")
    private lateinit var objectMapper: ObjectMapper

    @Test
    @WithMockPrincipal(roles = [Authorities.BANK_READ_ME])
    internal fun `should be able to get own bank account`() {
        val username = ReactiveWebContext.getCurrentUser().map { it.username }.block()!!
        val expected = BankProjection(uuid(), username, 150)

        every { bankAccountService.getAccountByUsername(username) } returnsMonoOf expected

        val result = webTestClient
            .mutateWith(csrf())
            .get()
            .uri("/account/me")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody(BankProjection::class.java)
            .returnResult()

        assertEquals(expected, result.responseBody)
    }
}
