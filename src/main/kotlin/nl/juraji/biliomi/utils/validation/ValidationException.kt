package nl.juraji.biliomi.utils.validation

data class ValidationException(
        override val message: String,
) : IllegalArgumentException(message)
