package nl.juraji.biliomi.utils.validation

import reactor.core.publisher.Mono

/**
 * Initialize a new asynchronous validation block.
 *
 * @param block An [AsyncValidator] block.
 * @return A [Mono] resulting in `true` when validation succeeds
 * or a [Mono] of [ValidationException] when validation fails.
 */
fun validateAsync(block: AsyncValidator.() -> Unit): Mono<Boolean> = AsyncValidatorImpl().apply(block).run()

/**
 * Initialize a new validation block.
 *
 * @param block A [Validator] block.
 * @throws ValidationException When a validation fails.
 */
fun validate(block: Validator.() -> Unit): Validator = ValidatorImpl().apply(block)
