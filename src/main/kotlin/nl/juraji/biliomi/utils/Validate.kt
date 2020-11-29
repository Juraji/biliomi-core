package nl.juraji.biliomi.utils

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

object Validate {
    fun isTrue(assertion: Boolean, message: () -> String) {
        if (!assertion) fail(message)
    }

    fun isFalse(assertion: Boolean, message: () -> String) {
        if (assertion) fail(message)
    }

    fun isNotNull(value: Any?, message: () -> String) {
        if (value == null) fail(message)
    }

    fun ignoreWhen(predicate: Boolean, validation: Validate.() -> Unit) {
        if (!predicate) validation.invoke(this)
    }

    fun isNotBlank(value: String?, message: () -> String) {
        if (value.isNullOrBlank()) fail(message)
    }

    fun isNotEmpty(value: Collection<Any>, message: () -> String) {
        if (value.isEmpty()) fail(message)
    }

    fun fail(message: () -> String): Nothing = throw ValidationException(message())
}

object ValidateAsync {
    private fun success(): Mono<Boolean> = Mono.just(true)
    private fun fail(message: () -> String): Mono<Boolean> = Mono.error { ValidationException(message()) }

    fun isTrue(assertion: Mono<Boolean>, message: () -> String): Mono<Boolean> =
            assertion.flatMap { isTrue ->
                if (isTrue) success()
                else fail(message)
            }

    fun isFalse(assertion: Mono<Boolean>, message: () -> String): Mono<Boolean> =
            assertion.flatMap { isTrue ->
                if (!isTrue) success()
                else fail(message)
            }

    fun <T : Any> isNotNull(value: Mono<T>, message: () -> String): Mono<Boolean> =
            value.flatMap { success() }.switchIfEmpty { fail(message) }

    fun isNotBlank(value: Mono<CharSequence>, message: () -> String) = value
            .filter(CharSequence::isNotBlank)
            .flatMap { success() }
            .switchIfEmpty { fail(message) }

    fun ignoreWhen(predicate: Boolean, validation: ValidateAsync.() -> Mono<Boolean>): Mono<Boolean> =
            if (predicate) success() else validation.invoke(this)

    fun all(vararg validations: Mono<Boolean>): Mono<Boolean> =
            Flux.fromArray(validations).flatMap { x -> x }.all { x -> x }.flatMap { success() }

    fun all(block: Validate.() -> Unit): Mono<Boolean> = Mono.just(block)
            .map(Validate::apply)
            .flatMap { success() }
}

fun <T : Any> Mono<T>.validate(block: Validate.(T) -> Unit): Mono<T> =
        this.map { next -> Validate.apply { block.invoke(this, next) }.let { next } }

fun <T : Any> Flux<T>.validate(block: Validate.(T) -> Unit): Flux<T> =
        this.map { next -> Validate.apply { block.invoke(this, next) }.let { next } }

data class ValidationException(
        override val message: String,
) : IllegalArgumentException(message)
