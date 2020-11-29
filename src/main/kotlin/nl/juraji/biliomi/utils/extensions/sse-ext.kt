package nl.juraji.biliomi.utils.extensions

import org.springframework.http.codec.ServerSentEvent
import reactor.core.publisher.Flux
import java.time.Duration

typealias ServerSentEventFlux<T> = Flux<ServerSentEvent<T?>>

fun <T : Any> Flux<T>.toServerSentEvents(
        heartbeatDelay: Duration = Duration.ZERO,
        heartbeatInterval: Duration = Duration.ofSeconds(10),
): ServerSentEventFlux<T> {

    val sourceStream: ServerSentEventFlux<T> = this.map {
        ServerSentEvent
                .builder(it)
                .id(uuid())
                .build()
    }

    val heartbeatStream: ServerSentEventFlux<T> = Flux
            .interval(heartbeatDelay, heartbeatInterval)
            .map {
                ServerSentEvent
                        .builder<T>()
                        .event("ping")
                        .build()
            }

    return Flux.merge(heartbeatStream, sourceStream)
}
