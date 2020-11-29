package nl.juraji.biliomi.utils.validation

fun validate(block: Validator.() -> Unit): Validator = ValidatorImpl().apply(block)

internal class ValidatorImpl : Validator {
    override fun isTrue(assertion: Boolean, message: () -> String) {
        if (!assertion) fail(message)
    }

    override fun isFalse(assertion: Boolean, message: () -> String) {
        if (assertion) fail(message)
    }

    override fun isNotNull(value: Any?, message: () -> String) {
        if (value == null) fail(message)
    }

    override fun ignoreWhen(predicate: Boolean, validation: Validator.() -> Unit) {
        if (!predicate) validation.invoke(this)
    }

    override fun isNotBlank(value: String?, message: () -> String) {
        if (value.isNullOrBlank()) fail(message)
    }

    override fun isNotEmpty(value: Collection<Any>, message: () -> String) {
        if (value.isEmpty()) fail(message)
    }

    override fun fail(message: () -> String): Nothing = throw ValidationException(message())
}
