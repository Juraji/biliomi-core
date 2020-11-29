package nl.juraji.biliomi.utils.validation

interface Validator {
    fun isTrue(assertion: Boolean, message: () -> String)
    fun isFalse(assertion: Boolean, message: () -> String)
    fun isNotNull(value: Any?, message: () -> String)
    fun isNotBlank(value: String?, message: () -> String)
    fun isNotEmpty(value: Collection<Any>, message: () -> String)
    fun ignoreWhen(predicate: Boolean, validation: Validator.() -> Unit)
    fun fail(message: () -> String): Nothing = throw ValidationException(message())
}
