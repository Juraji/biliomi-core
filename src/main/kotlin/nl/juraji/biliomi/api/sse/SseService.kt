package nl.juraji.biliomi.api.sse

import nl.juraji.biliomi.configuration.ProcessingGroups
import nl.juraji.biliomi.domain.DomainEvent
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks

@Service
@ProcessingGroup(ProcessingGroups.SSE)
class SseService {
    private val updatesProcessor: Sinks.Many<DomainEvent> = Sinks.many().multicast().directBestEffort()

    @EventHandler
    fun on(e: DomainEvent) {
        updatesProcessor.tryEmitNext(e)
    }

    fun getEvents(): Flux<DomainEvent> = updatesProcessor.asFlux()
}
