package nl.juraji.biliomi.utils.validation

import reactor.core.publisher.Mono

interface AsyncValidator {
    fun isTrue(assertion: Mono<Boolean>, message: () -> String)
    fun isFalse(assertion: Mono<Boolean>, message: () -> String)
    fun isNotEmpty(value: Mono<Any>, message: () -> String)
    fun isNotBlank(value: Mono<CharSequence>, message: () -> String)
    fun unless(predicate: Boolean, validation: AsyncValidator.() -> Unit)
    fun unless(predicate: Mono<Boolean>, validation: AsyncValidator.() -> Unit)
    fun synchronous(validation: Validator.() -> Unit)
}
