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
import java.time.LocalDate

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
    fun `can type single digit month without leading zero`() {
        cardExpirationDateElement.setText("2")
        expectThat(cardExpirationDateElement.getText()).isEqualTo("02/")

        val year = (LocalDate.now().year + 1).toString().takeLast(2)
        cardExpirationDateElement.setText("2$year")
        expectThat(cardExpirationDateElement.getText()).isEqualTo("02/$year")
    }

    @Test
    fun `does not add leading zero when first digit is 0 or 1`() {
        cardExpirationDateElement.setText("0")
        expectThat(cardExpirationDateElement.getText()).isEqualTo("0")

        cardExpirationDateElement.setText("1")
        expectThat(cardExpirationDateElement.getText()).isEqualTo("1")
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

        cardExpirationDateElement.setText("13/99")
        expectThat(changeEvents).single().and {
            get { isValid }.isFalse()
            get { isEmpty }.isFalse()
            get { isComplete }.isTrue()
        }
    }

    @Test
    fun `ChangeEvent is raised once when single digit month is entered`() {
        val changeEvents = mutableListOf<ChangeEvent>()
        cardExpirationDateElement.addChangeEventListener { changeEvents.add(it) }

        cardExpirationDateElement.setText("3")
        expectThat(changeEvents).single().and {
            get { isValid }.isFalse()
            get { isEmpty }.isFalse()
            get { isComplete }.isFalse()
        }
    }
}
