package com.basistheory.android.view

import android.app.Activity
import com.basistheory.android.event.ChangeEvent
import com.basistheory.android.model.ElementValueReference
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
        expectThat(cardExpirationDateElement.getTransformedText()).isEqualTo("") // note: EditText transforms nulls to ""

        cardExpirationDateElement.setText("")
        expectThat(cardExpirationDateElement.getTransformedText()).isEqualTo("")
    }

    @Test
    fun `applies mask when setting the value`() {
        cardExpirationDateElement.setText("1222")
        expectThat(cardExpirationDateElement.getTransformedText()).isEqualTo("12/22")
    }

    @Test
    fun `can type single digit month without leading zero`() {
        cardExpirationDateElement.setText("2")
        expectThat(cardExpirationDateElement.getTransformedText()).isEqualTo("02/")

        val year = (LocalDate.now().year + 1).toString().takeLast(2)
        cardExpirationDateElement.setText("2$year")
        expectThat(cardExpirationDateElement.getTransformedText()).isEqualTo("02/$year")
    }

    @Test
    fun `does not add leading zero when first digit is 0 or 1`() {
        cardExpirationDateElement.setText("0")
        expectThat(cardExpirationDateElement.getTransformedText()).isEqualTo("0")

        cardExpirationDateElement.setText("1")
        expectThat(cardExpirationDateElement.getTransformedText()).isEqualTo("1")
    }

    @Test
    fun `ChangeEvent and element metadata is computed properly for incomplete expiration dates`() {
        val changeEvents = mutableListOf<ChangeEvent>()
        cardExpirationDateElement.addChangeEventListener { changeEvents.add(it) }

        cardExpirationDateElement.setText("12")
        expectThat(changeEvents).single().and {
            get { isComplete }.isFalse()
            get { isValid }.isFalse()
            get { isEmpty }.isFalse()
            get { isMaskSatisfied }.isFalse()
        }

        expectThat(cardExpirationDateElement) {
            get { isComplete }.isFalse()
            get { isValid }.isFalse()
            get { isEmpty }.isFalse()
            get { isMaskSatisfied }.isFalse()
        }
    }

    @Test
    fun `ChangeEvent is computed properly for valid complete expiration dates`() {
        val changeEvents = mutableListOf<ChangeEvent>()
        cardExpirationDateElement.addChangeEventListener { changeEvents.add(it) }

        cardExpirationDateElement.setText("12/99")
        expectThat(changeEvents).single().and {
            get { isComplete }.isTrue()
            get { isValid }.isTrue()
            get { isMaskSatisfied }.isTrue()
            get { isEmpty }.isFalse()
        }

        expectThat(cardExpirationDateElement) {
            get { isComplete }.isTrue()
            get { isValid }.isTrue()
            get { isMaskSatisfied }.isTrue()
            get { isEmpty }.isFalse()
        }
    }

    @Test
    fun `ChangeEvent is computed properly for past expiration dates`() {
        val changeEvents = mutableListOf<ChangeEvent>()
        cardExpirationDateElement.addChangeEventListener { changeEvents.add(it) }

        cardExpirationDateElement.setText("01/01")
        expectThat(changeEvents).single().and {
            get { isComplete }.isFalse()
            get { isValid }.isFalse()
            get { isMaskSatisfied }.isTrue()
            get { isEmpty }.isFalse()
        }

        expectThat(cardExpirationDateElement) {
            get { isComplete }.isFalse()
            get { isValid }.isFalse()
            get { isMaskSatisfied }.isTrue()
            get { isEmpty }.isFalse()
        }
    }

    @Test
    fun `ChangeEvent is computed properly for invalid expiration dates`() {
        val changeEvents = mutableListOf<ChangeEvent>()
        cardExpirationDateElement.addChangeEventListener { changeEvents.add(it) }

        cardExpirationDateElement.setText("13/99")
        expectThat(changeEvents).single().and {
            get { isComplete }.isFalse()
            get { isValid }.isFalse()
            get { isMaskSatisfied }.isTrue()
            get { isEmpty }.isFalse()
        }

        expectThat(cardExpirationDateElement) {
            get { isComplete }.isFalse()
            get { isValid }.isFalse()
            get { isMaskSatisfied }.isTrue()
            get { isEmpty }.isFalse()
        }
    }

    @Test
    fun `ChangeEvent is raised once when single digit month is entered`() {
        val changeEvents = mutableListOf<ChangeEvent>()
        cardExpirationDateElement.addChangeEventListener { changeEvents.add(it) }

        cardExpirationDateElement.setText("3")
        expectThat(changeEvents).single().and {
            get { isComplete }.isFalse()
            get { isValid }.isFalse()
            get { isMaskSatisfied }.isFalse()
            get { isEmpty }.isFalse()
        }

        expectThat(cardExpirationDateElement) {
            get { isComplete }.isFalse()
            get { isValid }.isFalse()
            get { isMaskSatisfied }.isFalse()
            get { isEmpty }.isFalse()
        }
    }

    @Test
    fun `can set value references for both month and year ints`() {
        val monthRef = ElementValueReference { "8" }
        val yearRef = ElementValueReference { "2030" }
        cardExpirationDateElement.setValueRef(monthRef, yearRef)

        expectThat(cardExpirationDateElement.getText()).isEqualTo("08/30")
    }

    @Test
    fun `can set value references for both month and year as doubles`() {
        val monthRef = ElementValueReference { "8.0" }
        val yearRef = ElementValueReference { "2030.0" }
        cardExpirationDateElement.setValueRef(monthRef, yearRef)

        expectThat(cardExpirationDateElement.getText()).isEqualTo("08/30")
    }
}
