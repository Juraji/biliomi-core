package nl.juraji.biliomi.utils.validation

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ValidatorDslTest {

    @Test
    fun `isTrue should proceed when assertion is true`() {
        Assertions.assertDoesNotThrow {
            validate {
                isTrue(true) { "Should not throw" }
            }
        }
    }

    @Test
    fun `isTrue should fail when assertion is false`() {
        Assertions.assertThrows(ValidationException::class.java) {
            validate {
                isTrue(false) { "Should throw" }
            }
        }
    }

    @Test
    fun `isFalse should proceed when assertion is false`() {
        Assertions.assertDoesNotThrow {
            validate {
                isFalse(false) { "Should not throw" }
            }
        }
    }

    @Test
    fun `isFalse should fail when assertion is true`() {
        Assertions.assertThrows(ValidationException::class.java) {
            validate {
                isFalse(true) { "Should throw" }
            }
        }
    }

    @Test
    fun `isNotNull should proceed when value is not null`() {
        Assertions.assertDoesNotThrow {
            validate {
                isNotNull("Something") { "Should not throw" }
            }
        }
    }

    @Test
    fun `isNotNull should fail when value is null`() {
        Assertions.assertThrows(ValidationException::class.java) {
            validate {
                isNotNull(null) { "Should throw" }
            }
        }
    }

    @Test
    fun `isNotBlank should proceed when character sequence is not blank`() {
        Assertions.assertDoesNotThrow {
            validate {
                isNotBlank("Something") { "Should not throw" }
            }
        }
    }

    @Test
    fun `isNotBlank should fail when character sequence is blank`() {
        Assertions.assertThrows(ValidationException::class.java) {
            validate {
                isNotBlank("   ") { "Should throw" }
            }
        }
    }
}
