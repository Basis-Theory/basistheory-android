package com.basistheory.android.view.validation

import org.junit.Test
import org.threeten.bp.LocalDate
import strikt.api.expect
import strikt.assertions.isFalse
import strikt.assertions.isTrue

class FutureDateValidationTests {
    private val validator = FutureDateValidator()

    @Test
    fun `returns false for null or empty values`() {
        expect {
            that(validator.validate("")).isFalse()
            that(validator.validate(null)).isFalse()
        }
    }

    @Test
    fun `returns false for incomplete values`() {
        expect {
            that(validator.validate("01")).isFalse()
            that(validator.validate("01/")).isFalse()
            that(validator.validate("/")).isFalse()
            that(validator.validate("/22")).isFalse()
        }
    }

    @Test
    fun `returns false for invalid values`() {
        expect {
            that(validator.validate("invalid")).isFalse()
            that(validator.validate("1902873")).isFalse()
            that(validator.validate("12//25")).isFalse()
            that(validator.validate("00/00")).isFalse()
            that(validator.validate("99/25")).isFalse()
        }
    }

    @Test
    fun `returns true for valid dates in the past`() {
        expect {
            that(validator.validate("12/12")).isFalse()
            that(validator.validate("01/19")).isFalse()
            that(validator.validate("01/00")).isFalse()
            that(validator.validate("1/2")).isFalse()
        }
    }

    @Test
    fun `returns true for valid dates in the future`() {
        // note: we inject the "current" time to avoid test flakiness over time
        val currentDate = LocalDate.of(2022, 6, 1)
        val validator = FutureDateValidator(currentDate)

        expect {
            that(validator.validate("06/25", )).isTrue()
            that(validator.validate("07/22")).isTrue()
            that(validator.validate("7/22")).isTrue()
        }
    }

    @Test
    fun `returns true for valid dates in current month`() {
        // note: we inject the "current" time to avoid test flakiness over time
        expect {
            that(
                FutureDateValidator(LocalDate.of(2022, 12, 1))
                    .validate("12/22")
            ).isTrue()
            that(
                FutureDateValidator(LocalDate.of(2022, 12, 10))
                    .validate("12/22")
            ).isTrue()
            that(
                FutureDateValidator(LocalDate.of(2022, 12, 31))
                    .validate("12/22")
            ).isTrue()
        }
    }

    @Test
    fun `defaults to using current date`() {
        expect {
            that(validator.validate("11/22")).isFalse()
            that(validator.validate("11/99")).isTrue() // far enough into the future this should never fail
        }
    }
}