package com.basistheory.android.view.mask


import com.basistheory.android.model.InputAction
import org.junit.Test
import strikt.api.expect
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.*

class ElementMaskTests {

    @Test
    fun `throws IllegalArgumentException when mask contains strings longer than 1 char`() {
        expectCatching { ElementMask(listOf("foo")) }
            .isFailure()
            .isA<IllegalArgumentException>()
    }

    @Test
    fun `throws IllegalArgumentException when mask contains illegal mask input`() {
        expectCatching { ElementMask(listOf('f', Regex("."), object {})) }
            .isFailure()
            .isA<IllegalArgumentException>()
    }

    @Test
    fun `throws IllegalArgumentException when mask contains empty string`() {
        expectCatching { ElementMask(listOf("")) }
            .isFailure()
            .isA<IllegalArgumentException>()
    }

    @Test
    fun `throws IllegalArgumentException when mask is empty`() {
        expectCatching { ElementMask(emptyList()) }
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
        val mask = ElementMask(maskPattern)
        val maskedValue = mask.evaluate("2345678900", InputAction.INSERT)

        expect {
            that(maskedValue).isEqualTo("+1(234) 567-8900")
            that(mask.isSatisfied(maskedValue)).isTrue()
        }
    }

    @Test
    fun `mask is marked as done only when completed`() {
        val digitRegex = Regex("""\d""")
        val maskPattern = listOf(
            digitRegex,
            digitRegex
        )
        val mask = ElementMask(maskPattern)

        expect {
            that(mask.isSatisfied("2")).isFalse()
            that(mask.isSatisfied("23")).isTrue()
        }
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
        val mask = ElementMask(maskPattern)

        expectThat(mask.evaluate("e2e2e", InputAction.INSERT)).isEqualTo("e-2-e-2-e")
    }

    @Test
    fun `mask with underscore placeholder is applied correctly`() {
        val charRegex = Regex("""[A-Za-z]""")
        val maskPattern = listOf(
            charRegex,
            "_",
            charRegex
        )
        val mask = ElementMask(maskPattern)

        expect {
            that(mask.evaluate("AB", InputAction.INSERT)).isEqualTo("A_B")
            that(mask.evaluate("A_B", InputAction.INSERT)).isEqualTo("A_B")
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
        val mask = ElementMask(maskPattern)

        expect {
            that(mask.evaluate("+1(1", InputAction.INSERT)).isEqualTo("+1(1")
            that(mask.evaluate("1", InputAction.INSERT)).isEqualTo("+1(1")
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
        val mask = ElementMask(maskPattern)

        expect {
            that(mask.evaluate("12", InputAction.INSERT)).isEqualTo("1-2")
            that(mask.evaluate("1-2", InputAction.INSERT)).isEqualTo("1-2") // x
            that(mask.evaluate("AB", InputAction.INSERT)).isEqualTo("A-B")
            that(mask.evaluate("A-B", InputAction.INSERT)).isEqualTo("A-B")
            that(mask.evaluate("$#", InputAction.INSERT)).isEqualTo("$-#")
            that(mask.evaluate("$-#", InputAction.INSERT)).isEqualTo("$-#")
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
        val mask = ElementMask(maskPattern)

        expect {
            that(mask.evaluate("ee23dd", InputAction.INSERT)).isEqualTo("e-2-d")
            that(mask.evaluate("ee23234", InputAction.INSERT)).isEqualTo("e-2-")
            that(mask.evaluate("asdf", InputAction.INSERT)).isEqualTo("a-")
        }
    }

    @Test
    fun `mask with numeric placeholders is applied correctly`() {
        val maskPattern = listOf("#", "#", "#", "-", '#', '#', "-", "#", "#", "#", "#")
        val mask = ElementMask(maskPattern)

        expectThat(
            mask.evaluate(
                "123456789",
                InputAction.INSERT
            )
        ).isEqualTo("123-45-6789")
    }

    @Test
    fun `mask with mixed placeholders is applied correctly`() {
        val maskPattern = listOf("#", "#", "#", "-", "x", "x", "-", "*", "*", "*", "*")
        val mask = ElementMask(maskPattern)

        expect {
            that(
                mask.evaluate(
                    "123AB1234",
                    InputAction.INSERT
                )
            ).isEqualTo("123-AB-1234")
            that(
                mask.evaluate(
                    "123ABA1$#",
                    InputAction.INSERT
                )
            ).isEqualTo("123-AB-A1$#")
            that(
                mask.evaluate(
                    "1234ABA1$#",
                    InputAction.INSERT
                )
            ).isEqualTo("123-AB-A1$#")
        }
    }

    @Test
    fun `mask is not displayed when given empty input`() {
        val maskPattern = listOf(
            "(",
            Regex("""\d"""),
            ")"
        )
        val mask = ElementMask(maskPattern)

        expect {
            that(mask.evaluate("", InputAction.INSERT)).isEqualTo("")
            that(mask.evaluate("", InputAction.DELETE)).isEqualTo("")
        }
    }
}
