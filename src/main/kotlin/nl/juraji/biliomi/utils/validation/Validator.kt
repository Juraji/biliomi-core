package nl.juraji.biliomi.utils.validation

interface Validator {

    /**
     * Validate that [assertion] equals to `true`.
     *
     * @param assertion The [Boolean] value to validate.
     * @throws ValidationException When [assertion] equals to `false`.
     */
    fun isTrue(assertion: Boolean, message: () -> String)

    /**
     * Validate that [assertion] equals to `false`.
     *
     * @param assertion The [Boolean] value to validate.
     * @param message A [Function] which results in a descriptive message.
     * @throws ValidationException When [assertion] equals to `true`.
     */
    fun isFalse(assertion: Boolean, message: () -> String)

    /**
     * Validate that [value] is not `null` or [Unit].
     *
     * @param value The value to validate.
     * @param message A [Function] which results in a descriptive message.
     * @throws ValidationException When [value] equals to `null` or [Unit].
     */
    fun isNotNull(value: Any?, message: () -> String)

    /**
     * Validate that [value] is not `null` or blank.
     *
     * @param value The value to validate.
     * @param message A [Function] which results in a descriptive message.
     * @throws ValidationException When [value] equals to `null` or is blank.
     */
    fun isNotBlank(value: String?, message: () -> String)

    /**
     * Validate that [collection] is not empty.
     *
     * @param collection A [Collection].
     * @param message A [Function] which results in a descriptive message.
     * @throws ValidationException When [collection] is empty.
     */
    fun isNotEmpty(collection: Collection<Any>, message: () -> String)

    /**
     * Ignore [validation] block when [predicate] equals to `true`.
     *
     * @param predicate A [Boolean] value whether to run [validation] block or not.
     * @param validation A new [Validator] block.
     */
    fun unless(predicate: Boolean, validation: Validator.() -> Unit)

    /**
     * Fail with [message].
     *
     * @param message A [Function] which results in a descriptive message.
     * @throws ValidationException Always with [message] as exception message
     */
    fun fail(message: () -> String): Nothing
}
