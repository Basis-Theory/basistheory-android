package com.basistheory.android.view.mask


import org.junit.Test
import strikt.api.expect
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.*

class MaskTests {

    @Test
    fun `throws IllegalArgumentException when mask contains strings longer than 1 char`() {
        expectCatching { Mask(listOf("foo")) }
            .isFailure()
            .isA<IllegalArgumentException>()
    }

    @Test
    fun `throws IllegalArgumentException when mask contains illegal mask input`() {
        expectCatching { Mask(listOf('f', Regex("."), object {})) }
            .isFailure()
            .isA<IllegalArgumentException>()
    }

    @Test
    fun `throws IllegalArgumentException when mask contains empty string`() {
        expectCatching { Mask(listOf("")) }
            .isFailure()
            .isA<IllegalArgumentException>()
    }

    @Test
    fun `throws IllegalArgumentException when mask is empty`() {
        expectCatching { Mask(emptyList()) }
            .isFailure()
            .isA<IllegalArgumentException>()
    }

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

        val incompleteMaskResult = mask.apply(
            "2",
            Action.INSERT
        )

        expectThat(incompleteMaskResult.isComplete).isFalse()

        val completeMaskResult = mask.apply(
            "23",
            Action.INSERT
        )

        expectThat(completeMaskResult.isComplete).isTrue()
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

    @Test
    fun `mask with underscore placeholder is applied correctly`() {
        val charRegex = Regex("""[A-Za-z]""")
        val maskPattern = listOf(
            charRegex,
            "_",
            charRegex
        )
        val mask = Mask(maskPattern)

        expect {
            that(mask.apply("AB", Action.INSERT).maskedValue).isEqualTo("A_B")
            that(mask.apply("A_B", Action.INSERT).maskedValue).isEqualTo("A_B")
        }
    }

    @Test
    fun `typing chars in beginning of mask works as expected`() {
        val digitRegex = Regex("""\d""")
        val maskPattern = listOf(
            "+",
            "1",
            "(",
            digitRegex,
            digitRegex,
            digitRegex,
            ")"
        )
        val mask = Mask(maskPattern)

        expect {
            that(mask.apply("+1(1", Action.INSERT).maskedValue).isEqualTo("+1(1")
            that(mask.apply("1", Action.INSERT).maskedValue).isEqualTo("+1(1")
        }
    }

    @Test
    fun `mask can contain regex allowing any char`() {
        val anyRegex = Regex(".")
        val maskPattern = listOf(
            anyRegex,
            "-",
            anyRegex
        )
        val mask = Mask(maskPattern)

        expect {
            that(mask.apply("12", Action.INSERT).maskedValue).isEqualTo("1-2")
            that(mask.apply("1-2", Action.INSERT).maskedValue).isEqualTo("1-2") // x
            that(mask.apply("AB", Action.INSERT).maskedValue).isEqualTo("A-B")
            that(mask.apply("A-B", Action.INSERT).maskedValue).isEqualTo("A-B")
            that(mask.apply("$#", Action.INSERT).maskedValue).isEqualTo("$-#")
            that(mask.apply("$-#", Action.INSERT).maskedValue).isEqualTo("$-#")
        }
    }

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
            that(mask.apply("ee23dd", Action.INSERT).maskedValue).isEqualTo("e-2-d")
            that(mask.apply("ee23234", Action.INSERT).maskedValue).isEqualTo("e-2-")
            that(mask.apply("asdf", Action.INSERT).maskedValue).isEqualTo("a-")
        }
    }

    @Test
    fun `mask with numeric placeholders is applied correctly`() {
        val maskPattern = listOf("#", "#", "#", "-", '#', '#', "-", "#", "#", "#", "#")
        val mask = Mask(maskPattern)

        expectThat(mask.apply("123456789", Action.INSERT).maskedValue).isEqualTo("123-45-6789")
    }

    @Test
    fun `mask with mixed placeholders is applied correctly`() {
        val maskPattern = listOf("#", "#", "#", "-", "x", "x", "-", "*", "*", "*", "*")
        val mask = Mask(maskPattern)

        expect {
            that(mask.apply("123AB1234", Action.INSERT).maskedValue).isEqualTo("123-AB-1234")
            that(mask.apply("123ABA1$#", Action.INSERT).maskedValue).isEqualTo("123-AB-A1$#")
            that(mask.apply("1234ABA1$#", Action.INSERT).maskedValue).isEqualTo("123-AB-A1$#")
        }
    }

    @Test
    fun `mask is not displayed when given empty input`() {
        val maskPattern = listOf(
            "(",
            Regex("""\d"""),
            ")"
        )
        val mask = Mask(maskPattern)

        expect {
            that(mask.apply("", Action.INSERT).maskedValue).isEqualTo("")
            that(mask.apply("", Action.DELETE).maskedValue).isEqualTo("")
        }
    }
}
