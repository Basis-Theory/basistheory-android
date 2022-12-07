package com.basistheory.android.view

import android.app.Activity
import com.basistheory.android.event.ChangeEvent
import com.basistheory.android.view.transform.regexReplaceElementTransform
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
class CardVerificationCodeElementTests {
    private lateinit var cvcElement: CardVerificationCodeElement

    @Before
    fun setUp() {
        val activity = Robolectric.buildActivity(Activity::class.java).get()
        cvcElement = CardVerificationCodeElement(activity)
    }

    @Test
    fun `can clear the value`() {
        cvcElement.setText(null)
        expectThat(cvcElement.getText()).isEqualTo("") // note: EditText transforms nulls to ""

        cvcElement.setText("")
        expectThat(cvcElement.getText()).isEqualTo("")
    }

    @Test
    fun `applies mask when setting the value`() {
        cvcElement.setText("1a2b3c")
        expectThat(cvcElement.getText()).isEqualTo("123")
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
}