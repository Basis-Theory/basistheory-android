package com.basistheory.android.view.validation

import org.junit.Test
import strikt.api.expect
import strikt.api.expectCatching
import strikt.assertions.isA
import strikt.assertions.isFailure
import strikt.assertions.isFalse
import strikt.assertions.isTrue

class RegexValidatorTests {

    @Test
    fun `should return true for values matching regex`() {
        val validator = RegexValidator("""^\d{3,4}$""")

        expect {
            that(validator.validate("123")).isTrue()
            that(validator.validate("000")).isTrue()
            that(validator.validate("1234")).isTrue()
            that(validator.validate("0000")).isTrue()
        }
    }

    @Test
    fun `should return false for values not matching regex`() {
        val validator = RegexValidator("""^\d{3,4}$""")

        expect {
            that(validator.validate("foo")).isFalse()
            that(validator.validate("1")).isFalse()
            that(validator.validate("12345")).isFalse()
            that(validator.validate(" 123")).isFalse()
            that(validator.validate("a123")).isFalse()
            that(validator.validate("123z")).isFalse()
        }
    }

    @Test
    fun `should return false for empty values`() {
        val validator = RegexValidator("""^\d{3,4}$""")

        expect {
            that(validator.validate("")).isFalse()
            that(validator.validate(null)).isFalse()
        }
    }

    @Test
    fun `throws when initialized with invalid regex pattern`() {
        expectCatching { RegexValidator("[") }
            .isFailure()
            .isA<IllegalArgumentException>()
    }
}
