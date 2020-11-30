package nl.juraji.biliomi.utils.validation

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

    override fun unless(predicate: Boolean, validation: Validator.() -> Unit) {
        if (!predicate) validation.invoke(this)
    }

    override fun isNotBlank(value: String?, message: () -> String) {
        if (value.isNullOrBlank()) fail(message)
    }

    override fun isNotEmpty(collection: Collection<Any>, message: () -> String) {
        if (collection.isEmpty()) fail(message)
    }

    override fun fail(message: () -> String): Nothing = throw ValidationException(message())
}
