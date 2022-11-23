package com.basistheory.android.view.transform

import org.junit.Test
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isFailure
import strikt.assertions.isNull

class RegexReplaceElementTransformTests {
    @Test
    fun `null values are not transformed`() {
        val transform = RegexReplaceElementTransform(Regex("[\\s]"))

        expectThat(transform.apply(null)).isNull()
    }

    @Test
    fun `empty values are not transformed`() {
        val transform = RegexReplaceElementTransform(Regex("[\\s]"))

        expectThat(transform.apply("")).isEqualTo("")
    }

    @Test
    fun `text is not transformed when transform regex has no match`() {
        val transform = RegexReplaceElementTransform(Regex("[\\s]"))

        expectThat(transform.apply("foo123")).isEqualTo("foo123")
    }

    @Test
    fun `text is transformed when transform regex has matches`() {
        val transform = RegexReplaceElementTransform(Regex("[^\\d]"))

        expectThat(transform.apply("(123) 456-7890")).isEqualTo("1234567890")
    }

    @Test
    fun `constructor throws when given an invalid regex pattern`() {
        // kotlin is too smart for its own good here; it attempts to prevent invalid
        //  regex from being compiled; .plus("[") breaks up the regex string and skips this
        // design/compile-time validation
        val invalidRegex = "*".plus("[")

        expectCatching { RegexReplaceElementTransform(Regex(invalidRegex)) }
            .isFailure()
            .isA<IllegalArgumentException>()
    }
}
