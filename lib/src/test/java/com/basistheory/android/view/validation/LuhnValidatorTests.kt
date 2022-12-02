package com.basistheory.android.view.validation

import org.junit.Test
import strikt.api.expect
import strikt.assertions.isFalse
import strikt.assertions.isTrue

class LuhnValidatorTests {

    @Test
    fun `should return true for valid card numbers`() {
        expect {
            that(luhnValidator("4242424242424242")).isTrue()
            that(luhnValidator("5555555555554444")).isTrue()
            that(luhnValidator("6011000990139424")).isTrue()
            that(luhnValidator("378282246310005")).isTrue()
        }
    }

    @Test
    fun `should return false for empty card numbers`() {
        expect {
            that(luhnValidator("")).isFalse()
            that(luhnValidator(null)).isFalse()
        }
    }

    @Test
    fun `should return false for non numeric values`() {
        expect {
            that(luhnValidator("foo")).isFalse()
            that(luhnValidator("asdf123l;kj")).isFalse()
        }
    }

    @Test
    fun `should return false for non-Luhn valid cards`() {
        expect {
            that(luhnValidator("5200828282828211")).isFalse()
            that(luhnValidator("5555555555554443")).isFalse()
        }
    }
}