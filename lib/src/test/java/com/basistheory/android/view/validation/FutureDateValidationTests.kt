package com.basistheory.android.view.validation

import org.junit.Test
import org.threeten.bp.LocalDate
import strikt.api.expect
import strikt.assertions.isFalse
import strikt.assertions.isTrue

class FutureDateValidationTests {

    @Test
    fun `returns false for null or empty values`() {
        expect {
            that(futureDateValidator("")).isFalse()
            that(futureDateValidator(null)).isFalse()
        }
    }

    @Test
    fun `returns false for incomplete values`() {
        expect {
            that(futureDateValidator("01")).isFalse()
            that(futureDateValidator("01/")).isFalse()
            that(futureDateValidator("/")).isFalse()
            that(futureDateValidator("/22")).isFalse()
        }
    }

    @Test
    fun `returns false for invalid values`() {
        expect {
            that(futureDateValidator("invalid")).isFalse()
            that(futureDateValidator("1902873")).isFalse()
            that(futureDateValidator("12//25")).isFalse()
            that(futureDateValidator("00/00")).isFalse()
            that(futureDateValidator("99/25")).isFalse()
        }
    }

    @Test
    fun `returns true for valid dates in the past`() {
        expect {
            that(futureDateValidator("12/12")).isFalse()
            that(futureDateValidator("01/19")).isFalse()
            that(futureDateValidator("01/00")).isFalse()
            that(futureDateValidator("1/2")).isFalse()
        }
    }

    @Test
    fun `returns true for valid dates in the future`() {
        // note: we inject the "current" time to avoid test flakiness over time
        val currentDate = LocalDate.of(2022, 6, 1)
        expect {
            that(futureDateValidator("06/25", currentDate)).isTrue()
            that(futureDateValidator("07/22", currentDate)).isTrue()
            that(futureDateValidator("7/22", currentDate)).isTrue()
        }
    }

    @Test
    fun `returns true for valid dates in current month`() {
        // note: we inject the "current" time to avoid test flakiness over time
        expect {
            that(
                futureDateValidator(
                    "12/22",
                    LocalDate.of(2022, 12, 1)
                )
            ).isTrue()
            that(
                futureDateValidator(
                    "12/22",
                    LocalDate.of(2022, 12, 10)
                )
            ).isTrue()
            that(
                futureDateValidator(
                    "12/22",
                    LocalDate.of(2022, 12, 31)
                )
            ).isTrue()
        }
    }

    @Test
    fun `defaults to using current date`() {
        expect {
            that(futureDateValidator("11/22")).isFalse()
            that(futureDateValidator("11/99")).isTrue() // far enough into the future this should never fail
        }
    }
}