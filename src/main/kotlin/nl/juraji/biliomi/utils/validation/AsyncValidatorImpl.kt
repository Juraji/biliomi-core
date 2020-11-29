package nl.juraji.biliomi.utils.validation

import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

fun validateAsync(block: AsyncValidator.() -> Unit): Mono<Boolean> = AsyncValidatorImpl().apply(block).run()

internal class AsyncValidatorImpl : AsyncValidator {
    private val collectedMono: MutableList<Mono<Boolean>> = mutableListOf()

    override fun isTrue(assertion: Mono<Boolean>, message: () -> String) = collect {
        assertion.flatMap { b ->
            if (b) success()
            else fail(message)
        }
    }

    override fun isFalse(assertion: Mono<Boolean>, message: () -> String) = collect {
        assertion.flatMap { b ->
            if (!b) success()
            else fail(message)
        }
    }

    override fun isNotEmpty(value: Mono<Any>, message: () -> String) = collect {
        value
                .flatMap { success() }
                .switchIfEmpty { fail(message) }
    }

    override fun isNotBlank(value: Mono<CharSequence>, message: () -> String) = collect {
        value
                .filter(CharSequence::isNotBlank)
                .flatMap { success() }
                .switchIfEmpty { fail(message) }
    }

    override fun ignoreWhen(predicate: Boolean, validation: AsyncValidator.() -> Mono<Boolean>) = collect {
        if (predicate) success()
        else validation.invoke(this)
    }

    override fun synchronous(block: Validator.() -> Unit) = collect {
        Mono.just(block)
                .map { ValidatorImpl().apply(it) }
                .flatMap { success() }
    }

    fun run(): Mono<Boolean> = Mono.zip(collectedMono) { values -> values.all { it == true } }

    private fun collect(creator: () -> Mono<Boolean>) {
        collectedMono.add(creator.invoke())
    }

    private fun success(): Mono<Boolean> = Mono.just(true)
    private fun fail(message: () -> String): Mono<Boolean> = Mono.error { ValidationException(message()) }
}
