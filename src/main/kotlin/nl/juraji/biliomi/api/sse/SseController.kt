package nl.juraji.biliomi.api.sse

import nl.juraji.biliomi.configuration.security.Authorities
import nl.juraji.biliomi.domain.DomainEvent
import nl.juraji.biliomi.utils.extensions.ServerSentEventFlux
import nl.juraji.biliomi.utils.extensions.toServerSentEvents
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/events")
class SseController(
    private val sseService: SseService
) {

    @GetMapping
    @PreAuthorize("hasRole('${Authorities.SSE_CONNECT}')")
    fun getEvents(): ServerSentEventFlux<DomainEvent> = sseService
        .getEvents()
        .toServerSentEvents()
}
