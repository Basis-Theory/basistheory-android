package com.basistheory.android.view

import android.app.Activity
import com.basistheory.android.event.ChangeEvent
import com.basistheory.android.model.ElementValueReference
import com.basistheory.android.view.mask.ElementMask
import com.basistheory.android.view.transform.RegexReplaceElementTransform
import com.basistheory.android.view.validation.RegexValidator
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
    private lateinit var otherTextElement: TextElement

    @Before
    fun setUp() {
        val activity = Robolectric.buildActivity(Activity::class.java).get()
        textElement = TextElement(activity)
        otherTextElement = TextElement(activity)
    }

    @Test
    fun `can clear the value`() {
        textElement.transform = RegexReplaceElementTransform(Regex("[\\s]"))

        textElement.setText(null)
        expectThat(textElement.getText()).isEqualTo("") // note: EditText transforms nulls to ""
        expectThat(textElement.getTransformedText()).isEqualTo("") // note: EditText transforms nulls to ""

        textElement.setText("")
        expectThat(textElement.getText()).isEqualTo("")
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
    fun `untransformed text value is internally accessible`() {
        textElement.transform = RegexReplaceElementTransform(Regex("[^\\d]"))
        textElement.setText("(1")
        expectThat(textElement.getText()).isEqualTo("(1")

        textElement.transform = RegexReplaceElementTransform(Regex("[()\\s2]"))
        textElement.setText("(123) 4")
        expectThat(textElement.getText()).isEqualTo("(123) 4")

        textElement.transform = RegexReplaceElementTransform(Regex("[()]"))
        textElement.setText("(123) 456-7890")
        expectThat(textElement.getText()).isEqualTo("(123) 456-7890")
    }

    @Test
    fun `can apply mask`() {
        val digitRegex = Regex("""\d""")
        textElement.mask = ElementMask(
            listOf(
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
        )
        textElement.setText("2345678900")
        expectThat(textElement.getText()).isEqualTo("+1(234) 567-8900")
    }

    @Test
    fun `computes isValid`() {
        expectThat(textElement.isValid).isTrue()

        textElement.validator = RegexValidator("""\d{3}""")
        expectThat(textElement.isValid).isFalse()

        textElement.setText("123")
        expectThat(textElement.isValid).isTrue()

        textElement.setText("12")
        expectThat(textElement.isValid).isFalse()
    }

    @Test
    fun `computes isMaskSatisfied`() {
        expectThat(textElement.isMaskSatisfied).isTrue()

        textElement.mask = ElementMask("###")
        expectThat(textElement.isMaskSatisfied).isFalse()

        textElement.setText("123")
        expectThat(textElement.isMaskSatisfied).isTrue()

        textElement.setText("12")
        expectThat(textElement.isMaskSatisfied).isFalse()
    }

    @Test
    fun `computes isComplete`() {
        expectThat(textElement.isComplete).isTrue()

        textElement.mask = ElementMask("***")
        textElement.validator = RegexValidator("""\d{3}""")

        textElement.setText("abc")
        expectThat(textElement.isComplete).isFalse()

        textElement.setText("123")
        expectThat(textElement.isComplete).isTrue()
    }

    @Test
    fun `computes isEmpty`() {
        expectThat(textElement.isEmpty).isTrue()

        textElement.setText("foo")
        expectThat(textElement.isEmpty).isFalse()
    }

    @Test
    fun `can use TextElement without a mask, transform, or validator`() {
        val changeEvents = mutableListOf<ChangeEvent>()

        textElement.addChangeEventListener { changeEvents.add(it) }

        textElement.setText("123")
        expectThat(textElement.getText()).isEqualTo("123")

        expectThat(changeEvents).single().and {
            get { isComplete }.isTrue()
            get { isValid }.isTrue()
            get { isMaskSatisfied }.isTrue()
            get { isEmpty }.isFalse()
        }

        expectThat(textElement) {
            get { isComplete }.isTrue()
            get { isValid }.isTrue()
            get { isMaskSatisfied }.isTrue()
            get { isEmpty }.isFalse()
        }
    }

    @Test
    fun `can reference the value of another TextElement`() {
        textElement.setValueRef(otherTextElement)

        otherTextElement.setText("123")
        expectThat(textElement.getText()).isEqualTo("123")

        otherTextElement.setText("(123) 456-7890")
        expectThat(textElement.getText()).isEqualTo("(123) 456-7890")
    }

    @Test
    fun `can reference the value of a value reference`() {
        val valueReference = ElementValueReference { "4242424242424242" }
        textElement.setValueRef(valueReference)

        expectThat(textElement.getText()).isEqualTo("4242424242424242")
    }
}
