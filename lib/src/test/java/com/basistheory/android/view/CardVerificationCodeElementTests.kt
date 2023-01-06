package com.basistheory.android.view

import android.app.Activity
import com.basistheory.android.event.ChangeEvent
import io.mockk.spyk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import strikt.api.expectThat
import strikt.assertions.*
import kotlin.text.get

@RunWith(RobolectricTestRunner::class)
class CardVerificationCodeElementTests {
    private lateinit var cvcElement: CardVerificationCodeElement

    private lateinit var cardNumberElement: CardNumberElement

    @Before
    fun setUp() {
        val activity = Robolectric.buildActivity(Activity::class.java).get()
        cvcElement = CardVerificationCodeElement(activity)
        cardNumberElement = CardNumberElement(activity)
    }

    @Test
    fun `can clear the value`() {
        cvcElement.setText(null)
        expectThat(cvcElement.getTransformedText()).isEqualTo("") // note: EditText transforms nulls to ""

        cvcElement.setText("")
        expectThat(cvcElement.getTransformedText()).isEqualTo("")
    }

    @Test
    fun `applies default mask when setting the value and no card number element is attached`() {
        cvcElement.cardNumberElement = null

        cvcElement.setText("1a2b3c")
        expectThat(cvcElement.getTransformedText()).isEqualTo("123")
    }

    @Test
    fun `updates mask depending on card brand`() {
        cvcElement.cardNumberElement = cardNumberElement

        cardNumberElement.setText("42")
        cvcElement.setText("1a2b3c4d5e")
        expectThat(cvcElement.getTransformedText()).isEqualTo("123")

        cardNumberElement.setText("34")
        cvcElement.setText("1a2b3c4d5e")
        expectThat(cvcElement.getTransformedText()).isEqualTo("1234")
    }

    @Test
    fun `ChangeEvent is computed properly for incomplete cvc values`() {
        val changeEvents = mutableListOf<ChangeEvent>()
        cvcElement.addChangeEventListener { changeEvents.add(it) }

        cvcElement.setText("12")
        expectThat(changeEvents).single().and {
            get { isValid }.isFalse()
            get { isEmpty }.isFalse()
            get { isComplete }.isFalse()
        }
    }

    @Test
    fun `ChangeEvent is computed properly for 3 digit cvc values`() {
        val changeEvents = mutableListOf<ChangeEvent>()
        cvcElement.addChangeEventListener { changeEvents.add(it) }

        cvcElement.setText("123")
        expectThat(changeEvents).single().and {
            get { isValid }.isTrue()
            get { isEmpty }.isFalse()
            get { isComplete }.isTrue()
        }
    }

    @Test
    fun `setting card number element multiple times does not duplicate listeners`() {
        val cardNumberElement = spyk(CardNumberElement(Robolectric.buildActivity(Activity::class.java).get()))
        cvcElement.cardNumberElement = cardNumberElement
        cvcElement.cardNumberElement = cardNumberElement

        verify(exactly = 1) { cardNumberElement.addChangeEventListener(any()) }
    }

    @Test
    fun `emits a change event when mask increases from 3 to 4 chars`() {
        val changeEvents = mutableListOf<ChangeEvent>()
        cvcElement.addChangeEventListener { changeEvents.add(it) }

        cardNumberElement.setText("4242424242424242")
        cvcElement.cardNumberElement = cardNumberElement
        cvcElement.setText("123")

        expectThat(changeEvents).single().and {
            get { isValid }.isTrue()
            get { isEmpty }.isFalse()
            get { isComplete }.isTrue()
        }

        // change the card brand from visa to american-express
        cardNumberElement.setText("378282246310005")

        expectThat(cvcElement.getText()).isEqualTo("123") // value does not change
        expectThat(changeEvents).hasSize(2).last().and {
            get { isValid }.isFalse()
            get { isEmpty }.isFalse()
            get { isComplete }.isFalse()
        }
    }

    @Test
    fun `emits a change event when mask decreases from 4 to 3 chars`() {
        val changeEvents = mutableListOf<ChangeEvent>()
        cvcElement.addChangeEventListener { changeEvents.add(it) }

        cardNumberElement.setText("378282246310005")
        cvcElement.cardNumberElement = cardNumberElement
        cvcElement.setText("1234")

        expectThat(changeEvents).single().and {
            get { isValid }.isTrue()
            get { isEmpty }.isFalse()
            get { isComplete }.isTrue()
        }

        // change the card brand from visa to american-express
        cardNumberElement.setText("4242424242424242")

        expectThat(cvcElement.getText()).isEqualTo("1234") // value does not change
        expectThat(changeEvents).hasSize(2).last().and {
            get { isValid }.isFalse()
            get { isEmpty }.isFalse()
            get { isComplete }.isFalse()
        }
    }

    @Test
    fun `does NOT emit a change event when card brand changes without impacting cvc mask`() {
        val changeEvents = mutableListOf<ChangeEvent>()
        cvcElement.addChangeEventListener { changeEvents.add(it) }

        cardNumberElement.setText("4242424242424242")
        cvcElement.cardNumberElement = cardNumberElement
        cvcElement.setText("1234")

        expectThat(changeEvents).single().and {
            get { isValid }.isTrue()
            get { isEmpty }.isFalse()
            get { isComplete }.isTrue()
        }

        // change the card brand from visa to mastercard
        cardNumberElement.setText("5555555555554444")

        expectThat(cvcElement.getText()).isEqualTo("123") // value does not change
        expectThat(changeEvents).single() // no new change events were published
    }
}