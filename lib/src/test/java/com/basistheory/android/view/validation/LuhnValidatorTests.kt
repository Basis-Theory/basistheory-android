package com.basistheory.android.view.validation

import org.junit.Test
import strikt.api.expect
import strikt.assertions.isFalse
import strikt.assertions.isTrue

class LuhnValidatorTests {
    private val validator = LuhnValidator()

    @Test
    fun `should return true for valid card numbers`() {
        expect {
            that(validator.validate("4242424242424242")).isTrue()
            that(validator.validate("5555555555554444")).isTrue()
            that(validator.validate("6011000990139424")).isTrue()
            that(validator.validate("378282246310005")).isTrue()
        }
    }

    @Test
    fun `should return false for empty card numbers`() {
        expect {
            that(validator.validate("")).isFalse()
            that(validator.validate(null)).isFalse()
        }
    }

    @Test
    fun `should return false for non numeric values`() {
        expect {
            that(validator.validate("foo")).isFalse()
            that(validator.validate("asdf123l;kj")).isFalse()
        }
    }

    @Test
    fun `should return false for non-Luhn valid cards`() {
        expect {
            that(validator.validate("5200828282828211")).isFalse()
            that(validator.validate("5555555555554443")).isFalse()
        }
    }
}