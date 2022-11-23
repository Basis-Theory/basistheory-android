package com.basistheory.android.view.transform

import com.github.javafaker.Faker
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNull

class ElementTransformTests {
    @Test
    fun `null values are not transformed`() {
        val transform = ElementTransform()

        expectThat(transform.apply(null)).isNull()
    }

    @Test
    fun `empty values are not transformed`() {
        val transform = ElementTransform()

        expectThat(transform.apply(null)).isNull()
    }

    @Test
    fun `non-empty values are not transformed`() {
        val transform = ElementTransform()
        val value = Faker().lorem().word()

        expectThat(transform.apply(value)).isEqualTo(value)
    }
}
