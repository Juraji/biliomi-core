package nl.juraji.biliomi.utils.validation

import reactor.core.publisher.Mono

interface AsyncValidator {

    /**
     * Assert that [assertion] results in `true`.
     *
     * @param assertion A [Mono] resulting in a [Boolean].
     * @param message A [Function] which results in the validation exception message.
     */
    fun isTrue(assertion: Mono<Boolean>, message: () -> String)

    /**
     * Assert that [assertion] results in `false`.
     *
     * @param assertion A [Mono] resulting in a [Boolean].
     * @param message A [Function] which results in the validation exception message.
     */
    fun isFalse(assertion: Mono<Boolean>, message: () -> String)

    /**
     * Run [validation] block only when [predicate] is `false`.
     *
     * @param predicate A [Boolean] value whether to run [validation] or not.
     * @param validation A new [AsyncValidator] block.
     */
    fun unless(predicate: Boolean, validation: AsyncValidator.() -> Unit)

    /**
     * Run [validation] block only when [predicate] results in `false`.
     *
     * @param predicate A [Mono] of type [Boolean] whether to run [validation] or not.
     * @param validation A new [AsyncValidator] block.
     */
    fun unless(predicate: Mono<Boolean>, validation: AsyncValidator.() -> Unit)

    /**
     * Run a synchronous [Validator] block as part of this validation.
     *
     * @param validation A new [Validator] block
     */
    fun synchronous(validation: Validator.() -> Unit)
}
