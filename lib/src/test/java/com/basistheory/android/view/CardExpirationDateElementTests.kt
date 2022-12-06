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
class CardExpirationDateElementTests {
    private lateinit var cardExpirationDateElement: CardExpirationDateElement

    @Before
    fun setUp() {
        val activity = Robolectric.buildActivity(Activity::class.java).get()
        cardExpirationDateElement = CardExpirationDateElement(activity)
    }

    @Test
    fun `can clear the value`() {
        cardExpirationDateElement.setText(null)
        expectThat(cardExpirationDateElement.getText()).isEqualTo("") // note: EditText transforms nulls to ""

        cardExpirationDateElement.setText("")
        expectThat(cardExpirationDateElement.getText()).isEqualTo("")
    }

    @Test
    fun `applies mask when setting the value`() {
        cardExpirationDateElement.setText("1222")
        expectThat(cardExpirationDateElement.getText()).isEqualTo("12/22")
    }

    @Test
    fun `ChangeEvent is computed properly for incomplete expiration dates`() {
        val changeEvents = mutableListOf<ChangeEvent>()
        cardExpirationDateElement.addChangeEventListener { changeEvents.add(it) }

        cardExpirationDateElement.setText("12")
        expectThat(changeEvents).single().and {
            get { isValid }.isFalse()
            get { isEmpty }.isFalse()
            get { isComplete }.isFalse()
        }
    }

    @Test
    fun `ChangeEvent is computed properly for valid complete expiration dates`() {
        val changeEvents = mutableListOf<ChangeEvent>()
        cardExpirationDateElement.addChangeEventListener { changeEvents.add(it) }

        cardExpirationDateElement.setText("12/99")
        expectThat(changeEvents).single().and {
            get { isValid }.isTrue()
            get { isEmpty }.isFalse()
            get { isComplete }.isTrue()
        }
    }

    @Test
    fun `ChangeEvent is computed properly for past expiration dates`() {
        val changeEvents = mutableListOf<ChangeEvent>()
        cardExpirationDateElement.addChangeEventListener { changeEvents.add(it) }

        cardExpirationDateElement.setText("01/01")
        expectThat(changeEvents).single().and {
            get { isValid }.isFalse()
            get { isEmpty }.isFalse()
            get { isComplete }.isTrue()
        }
    }

    @Test
    fun `ChangeEvent is computed properly for invalid expiration dates`() {
        val changeEvents = mutableListOf<ChangeEvent>()
        cardExpirationDateElement.addChangeEventListener { changeEvents.add(it) }

        cardExpirationDateElement.setText("99/99")
        expectThat(changeEvents).single().and {
            get { isValid }.isFalse()
            get { isEmpty }.isFalse()
            get { isComplete }.isTrue()
        }
    }
}