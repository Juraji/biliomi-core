package nl.juraji.biliomi.utils.validation

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.test.StepVerifier

class ValidatorReactorExtTest {

    @Test
    internal fun `should not fail mono when validation passes`() {
        val mono = Mono.just("Something")
                .validate {
                    isNotBlank(it) { "Should pass" }
                }

        StepVerifier.create(mono)
                .expectNext("Something")
                .verifyComplete()
    }

    @Test
    internal fun `should fail mono when validation does not pass`() {
        val mono = Mono.just("   ")
                .validate {
                    isNotBlank(it) { "Should not pass" }
                }

        StepVerifier.create(mono)
                .expectError(ValidationException::class.java)
                .verify()
    }

    @Test
    internal fun `should not fail mono when async validation passes`() {
        val mono = Mono.just("Something")
                .validateAsync {
                    isTrue(it.isNotBlank().toMono()) { "Should not pass" }
                }

        StepVerifier.create(mono)
                .expectNext("Something")
                .verifyComplete()
    }

    @Test
    internal fun `should fail mono when async validation does not pass`() {
        val mono = Mono.just("   ")
                .validateAsync {
                    isTrue(it.isNotBlank().toMono()) { "Should not pass" }
                }

        StepVerifier.create(mono)
                .expectError(ValidationException::class.java)
                .verify()
    }

    @Test
    internal fun `should not fail flux when validation passes`() {
        val mono = Flux.just("Something", "Something else")
                .validate {
                    isNotBlank(it) { "Should pass" }
                }

        StepVerifier.create(mono)
                .expectNext("Something")
                .expectNext("Something else")
                .verifyComplete()
    }

    @Test
    internal fun `should fail flux when validation does not pass on single item`() {
        val mono = Flux.just("Something", "  ", "Something else")
                .validate {
                    isNotBlank(it) { "Should not pass" }
                }

        StepVerifier.create(mono)
                .expectNext("Something")
                .expectError(ValidationException::class.java)
                .verify()
    }

    @Test
    internal fun `should not fail flux when async validation passes`() {
        val mono = Flux.just("Something", "Something else")
                .validateAsync {
                    isTrue(true.toMono()) { "Should pass" }
                }

        StepVerifier.create(mono)
                .expectNext("Something")
                .expectNext("Something else")
                .verifyComplete()
    }

    @Test
    internal fun `should fail flux when async validation does not pass on single item`() {
        val mono = Flux.just("Something", "  ", "Something else")
                .validateAsync {
                    isTrue(it.isNotBlank().toMono()) { "Should not pass" }
                }

        StepVerifier.create(mono)
                .expectNext("Something")
                .expectError(ValidationException::class.java)
                .verify()
    }
}
