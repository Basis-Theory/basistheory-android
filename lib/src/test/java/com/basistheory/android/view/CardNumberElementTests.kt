package com.basistheory.android.view

import android.app.Activity
import com.basistheory.android.event.ChangeEvent
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue
import strikt.assertions.single

@RunWith(RobolectricTestRunner::class)
class CardNumberElementTests {
    private lateinit var cardNumberElement: CardNumberElement

    @Before
    fun setUp() {
        val activity = Robolectric.buildActivity(Activity::class.java).get()
        cardNumberElement = CardNumberElement(activity)
    }

    @Test
    fun `can clear the value`() {
        cardNumberElement.setText(null)
        expectThat(cardNumberElement.getText()).isEqualTo("") // note: EditText transforms nulls to ""

        cardNumberElement.setText("")
        expectThat(cardNumberElement.getText()).isEqualTo("")
    }

    @Test
    fun `applies mask when setting the value`() {
        cardNumberElement.setText("4242abc4242def4242geh4242")
        expectThat(cardNumberElement.getText()).isEqualTo("4242424242424242")
    }

    @Test
    fun `applies the transform when retrieving the value`() {
        cardNumberElement.setText("4242 4242 4242 4242")
        expectThat(cardNumberElement.getText()).isEqualTo("4242424242424242")

        cardNumberElement.setText("4242-4242-4242-4242")
        expectThat(cardNumberElement.getText()).isEqualTo("4242424242424242")
    }

    @Test
    fun `ChangeEvent is computed properly for incomplete card numbers`() {
        val changeEvents = mutableListOf<ChangeEvent>()
        cardNumberElement.addChangeEventListener { changeEvents.add(it) }

        cardNumberElement.setText("1234 56")
        expectThat(changeEvents).single().and {
            get { isValid }.isFalse()
            get { isEmpty }.isFalse()
            get { isComplete }.isFalse()
        }
    }

    @Test
    fun `ChangeEvent is computed properly for valid complete card numbers`() {
        val changeEvents = mutableListOf<ChangeEvent>()
        cardNumberElement.addChangeEventListener { changeEvents.add(it) }

        cardNumberElement.setText("4242 4242 4242 4242")
        expectThat(changeEvents).single().and {
            get { isValid }.isTrue()
            get { isEmpty }.isFalse()
            get { isComplete }.isTrue()
        }
    }

    @Test
    fun `ChangeEvent is computed properly for invalid complete card numbers`() {
        val changeEvents = mutableListOf<ChangeEvent>()
        cardNumberElement.addChangeEventListener { changeEvents.add(it) }

        cardNumberElement.setText("4242 4242 4242 4243")
        expectThat(changeEvents).single().and {
            get { isValid }.isFalse()
            get { isEmpty }.isFalse()
            get { isComplete }.isTrue()
        }
    }
}