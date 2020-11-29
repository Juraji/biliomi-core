package nl.juraji.biliomi.utils.validation

import reactor.core.publisher.Mono

interface AsyncValidator {
    fun isTrue(assertion: Mono<Boolean>, message: () -> String)
    fun isFalse(assertion: Mono<Boolean>, message: () -> String)
    fun isNotEmpty(value: Mono<Any>, message: () -> String)
    fun isNotBlank(value: Mono<CharSequence>, message: () -> String)
    fun ignoreWhen(predicate: Boolean, validation: AsyncValidator.() -> Mono<Boolean>)
    fun synchronous(block: Validator.() -> Unit)
}
