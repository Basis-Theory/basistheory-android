package com.basistheory.android.view.validation

import org.junit.Test
import strikt.api.expect
import strikt.assertions.isFalse
import strikt.assertions.isTrue

class CardNumberValidatorTests {

    @Test
    fun `should return true for valid card numbers`() {
        expect {
            that(cardNumberValidator("4242424242424242")).isTrue()
            that(cardNumberValidator("5555555555554444")).isTrue()
            that(cardNumberValidator("6011000990139424")).isTrue()
            that(cardNumberValidator("378282246310005")).isTrue()
        }
    }

    @Test
    fun `should return false for empty card numbers`() {
        expect {
            that(cardNumberValidator("")).isFalse()
            that(cardNumberValidator(null)).isFalse()
        }
    }

    @Test
    fun `should return false for non numeric values`() {
        expect {
            that(cardNumberValidator("foo")).isFalse()
            that(cardNumberValidator("asdf123l;kj")).isFalse()
        }
    }

    @Test
    fun `should return false for non-Luhn valid cards`() {
        expect {
            that(cardNumberValidator("5200828282828211")).isFalse()
            that(cardNumberValidator("5555555555554443")).isFalse()
        }
    }
}