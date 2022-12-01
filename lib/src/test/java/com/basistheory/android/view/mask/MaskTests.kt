package com.basistheory.android.view.mask


import org.junit.Ignore
import org.junit.Test
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isTrue

class MaskTests {
    @Test
    fun `masks are applied correctly`() {
        val digitRegex = Regex("""\d""")
        val maskPattern = listOf(
            "+",
            "1",
            "(",
            digitRegex,
            digitRegex,
            digitRegex,
            ")",
            " ",
            digitRegex,
            digitRegex,
            digitRegex,
            "-",
            digitRegex,
            digitRegex,
            digitRegex,
            digitRegex
        )
        val mask = Mask(maskPattern)

        val maskResult = mask.apply(
            "2345678900",
            Action.INSERT
        )

        expectThat(maskResult) {
            get { maskedValue }.isEqualTo("+1(234) 567-8900")
            get { unMaskedValue }.isEqualTo("2345678900")
            get { isComplete }.isTrue()
        }
    }


    @Test
    fun `mask is marked as done only when completed`() {
        val digitRegex = Regex("""\d""")
        val maskPattern = listOf(
            digitRegex,
            digitRegex
        )
        val mask = Mask(maskPattern)

        val maskResult = mask.apply(
            "2",
            Action.INSERT
        )

        expectThat(
            maskResult.isComplete
        ).isEqualTo(false)
    }

    @Test
    fun `mask with regular expressions is applied correctly`() {
        val digitRegex = Regex("""\d""")
        val charRegex = Regex("""[A-Za-z]""")
        val maskPattern = listOf(
            charRegex,
            "-",
            digitRegex,
            "-",
            charRegex,
            "-",
            digitRegex,
            "-",
            charRegex,
        )
        val mask = Mask(maskPattern)

        expectThat(mask.apply("e2e2e", Action.INSERT).maskedValue).isEqualTo("e-2-e-2-e")
    }

    @Ignore("This isn't working yet")
    @Test
    fun `setting invalid characters ignores all invalid characters until the next valid char`() {
        val digitRegex = Regex("""\d""")
        val charRegex = Regex("""[A-Za-z]""")
        val maskPattern = listOf(
            charRegex,
            "-",
            digitRegex,
            "-",
            charRegex
        )
        val mask = Mask(maskPattern)

        expect {
            that(mask.apply("eee23dd", Action.INSERT).maskedValue).isEqualTo("e-2-d")
            that(mask.apply("eee23234", Action.INSERT).maskedValue).isEqualTo("e-2-")
            that(mask.apply("easdfasdf", Action.INSERT).maskedValue).isEqualTo("e-")
        }
    }

    @Test
    fun `mask with string placeholders is applied correctly`() {
        val maskPattern = listOf("#", "#", "#", "-", "#", "#", "-", "#", "#", "#", "#")
        val mask = Mask(maskPattern)

        expectThat(mask.apply("123456789", Action.INSERT).maskedValue).isEqualTo("123-45-6789")
    }
}
