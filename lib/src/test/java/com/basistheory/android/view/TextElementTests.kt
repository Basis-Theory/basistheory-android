package com.basistheory.android.view

import android.app.Activity
import com.basistheory.android.view.mask.ElementMask
import com.basistheory.android.view.transform.RegexReplaceElementTransform
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@RunWith(RobolectricTestRunner::class)
class TextElementTests {
    private lateinit var textElement: TextElement

    @Before
    fun setUp() {
        val activity = Robolectric.buildActivity(Activity::class.java).get()
        textElement = TextElement(activity)
    }

    @Test
    fun `can clear the value`() {
        textElement.transform = RegexReplaceElementTransform(Regex("[\\s]"))

        textElement.setText(null)
        expectThat(textElement.getText()).isEqualTo("") // note: EditText transforms nulls to ""

        textElement.setText("")
        expectThat(textElement.getText()).isEqualTo("")
    }

    @Test
    fun `can apply transform`() {
        textElement.transform = RegexReplaceElementTransform(Regex("[()\\-\\s]"))
        textElement.setText("(123) 456-7890")

        expectThat(textElement.getText()).isEqualTo("1234567890")
    }

    @Test
    fun `transform can be updated and text is transformed just in time`() {
        textElement.transform = RegexReplaceElementTransform(Regex("[^\\d]"))
        textElement.setText("(1")
        expectThat(textElement.getText()).isEqualTo("1")

        textElement.transform = RegexReplaceElementTransform(Regex("[()\\s2]"))
        textElement.setText("(123) 4")
        expectThat(textElement.getText()).isEqualTo("134")

        textElement.transform = RegexReplaceElementTransform(Regex("[()]"))
        textElement.setText("(123) 456-7890")
        expectThat(textElement.getText()).isEqualTo("123 456-7890")
    }

    @Test
    fun `can apply mask`() {
        val digitRegex = Regex("""\d""")
        textElement.mask = ElementMask(
            listOf("+", "1", "(", digitRegex,digitRegex,digitRegex, ")", " ", digitRegex, digitRegex, digitRegex, "-", digitRegex, digitRegex , digitRegex, digitRegex )
        )
        textElement.setText("2345678900")
        expectThat(textElement.getText()).isEqualTo("+1(234) 567-8900")
    }
}
