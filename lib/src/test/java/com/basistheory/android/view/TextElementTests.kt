package com.basistheory.android.view

import android.app.Activity
import com.basistheory.android.event.ChangeEvent
import com.basistheory.android.view.mask.ElementMask
import com.basistheory.android.view.transform.RegexReplaceElementTransform
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import strikt.api.expectThat
import strikt.assertions.*

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
        expectThat(textElement.getTransformedText()).isEqualTo("") // note: EditText transforms nulls to ""

        textElement.setText("")
        expectThat(textElement.getTransformedText()).isEqualTo("")
    }

    @Test
    fun `can apply transform`() {
        textElement.transform = RegexReplaceElementTransform(Regex("[()\\-\\s]"))
        textElement.setText("(123) 456-7890")

        expectThat(textElement.getTransformedText()).isEqualTo("1234567890")
    }

    @Test
    fun `transform can be updated and text is transformed just in time`() {
        textElement.transform = RegexReplaceElementTransform(Regex("[^\\d]"))
        textElement.setText("(1")
        expectThat(textElement.getTransformedText()).isEqualTo("1")

        textElement.transform = RegexReplaceElementTransform(Regex("[()\\s2]"))
        textElement.setText("(123) 4")
        expectThat(textElement.getTransformedText()).isEqualTo("134")

        textElement.transform = RegexReplaceElementTransform(Regex("[()]"))
        textElement.setText("(123) 456-7890")
        expectThat(textElement.getTransformedText()).isEqualTo("123 456-7890")
    }

    @Test
    fun `can apply mask`() {
        val digitRegex = Regex("""\d""")
        textElement.mask = ElementMask(
            listOf("+", "1", "(", digitRegex,digitRegex,digitRegex, ")", " ", digitRegex, digitRegex, digitRegex, "-", digitRegex, digitRegex , digitRegex, digitRegex )
        )
        textElement.setText("2345678900")
        expectThat(textElement.getTransformedText()).isEqualTo("+1(234) 567-8900")
    }

    @Test
    fun `can use TextElement without a mask, transform, or validator`() {
        val changeEvents = mutableListOf<ChangeEvent>()

        textElement.addChangeEventListener { changeEvents.add(it) }

        textElement.setText("123")
        expectThat(textElement.getTransformedText()).isEqualTo("123")

        expectThat(changeEvents).single().and {
            get { isComplete }.isFalse()
            get { isValid }.isTrue()
        }
    }
}
