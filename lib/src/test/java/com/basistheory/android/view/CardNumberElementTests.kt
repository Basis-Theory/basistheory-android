package com.basistheory.android.view

import android.app.Activity
import com.basistheory.android.constants.CardBrands
import com.basistheory.android.event.ChangeEvent
import com.basistheory.android.event.EventDetails
import com.basistheory.android.service.CardBrandEnricher
import com.basistheory.android.view.mask.ElementMask
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import strikt.api.expectThat
import strikt.assertions.*

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
        expectThat(cardNumberElement.getTransformedText()).isEqualTo("") // note: EditText transforms nulls to ""

        cardNumberElement.setText("")
        expectThat(cardNumberElement.getTransformedText()).isEqualTo("")
    }

    @Test
    fun `applies mask when setting the value`() {
        cardNumberElement.setText("4242abc4242def4242geh4242")
        expectThat(cardNumberElement.getTransformedText()).isEqualTo("4242424242424242")
    }

    @Test
    fun `applies mask based on card brand`() {
        cardNumberElement.setText("4242424242424242")
        expectThat(cardNumberElement) {
            get { mask }.isEqualTo(ElementMask(CardBrandEnricher.CardMasks.MASK_4_8_12GAPS_19DIGITS))
            get { isMaskSatisfied }.isTrue()
        }

        cardNumberElement.setText("55555555")
        expectThat(cardNumberElement) {
            get { mask }.isEqualTo(ElementMask(CardBrandEnricher.CardMasks.MASK_4_8_12GAPS_16DIGITS))
            get { isMaskSatisfied }.isFalse()
        }

        cardNumberElement.setText("371449635398431")
        expectThat(cardNumberElement) {
            get { mask }.isEqualTo(ElementMask(CardBrandEnricher.CardMasks.MASK_4_10GAPS_15DIGITS))
            get { isMaskSatisfied }.isTrue()
        }
    }

    @Test
    fun `mask can be satisfied for multiple lengths per brand`() {
        // discover valid lengths are 16 or 19
        val sixteenDigitsDiscoverCardNumber = "6582937163058334"

        cardNumberElement.setText(sixteenDigitsDiscoverCardNumber)
        expectThat(cardNumberElement.isMaskSatisfied).isTrue()

        cardNumberElement.setText("${sixteenDigitsDiscoverCardNumber}1")
        expectThat(cardNumberElement.isMaskSatisfied).isFalse()

        cardNumberElement.setText("${sixteenDigitsDiscoverCardNumber}12")
        expectThat(cardNumberElement.isMaskSatisfied).isFalse()

        cardNumberElement.setText("${sixteenDigitsDiscoverCardNumber}123")
        expectThat(cardNumberElement.isMaskSatisfied).isTrue()
    }

    @Test
    fun `applies the transform when retrieving the value`() {
        cardNumberElement.setText("4242 4242 4242 4242")
        expectThat(cardNumberElement.getTransformedText()).isEqualTo("4242424242424242")

        cardNumberElement.setText("4242-4242-4242-4242")
        expectThat(cardNumberElement.getTransformedText()).isEqualTo("4242424242424242")
    }

    @Test
    fun `ChangeEvent is computed properly for incomplete card numbers with invalid brand`() {
        val changeEvents = mutableListOf<ChangeEvent>()
        cardNumberElement.addChangeEventListener { changeEvents.add(it) }

        cardNumberElement.setText("1234 56")
        expectThat(changeEvents).single().and {
            get { isValid }.isFalse()
            get { isEmpty }.isFalse()
            get { isComplete }.isFalse()
            get { details }.isEmpty()
        }
    }

    @Test
    fun `ChangeEvent is computed properly for incomplete card numbers with valid brand`() {
        val changeEvents = mutableListOf<ChangeEvent>()
        cardNumberElement.addChangeEventListener { changeEvents.add(it) }

        cardNumberElement.setText("4123 45")
        expectThat(changeEvents).single().and {
            get { isValid }.isFalse()
            get { isEmpty }.isFalse()
            get { isComplete }.isFalse()
            get { details }.single().and {
                get { type }.isEqualTo(EventDetails.CardBrand)
                get { message }.isEqualTo(CardBrands.VISA.label)
            }
        }
    }

    @Test
    fun `ChangeEvent is computed properly for valid complete card numbers`() {
        val changeEvents = mutableListOf<ChangeEvent>()
        cardNumberElement.addChangeEventListener { changeEvents.add(it) }

        cardNumberElement.setText("4111 1111 1111 1111")
        expectThat(changeEvents).single().and {
            get { isValid }.isTrue()
            get { isEmpty }.isFalse()
            get { isMaskSatisfied }.isTrue()
            get { isComplete }.isTrue()
            get { details }.any {
                get { type }.isEqualTo(EventDetails.CardBrand)
                get { message }.isEqualTo(CardBrands.VISA.label)
            }
            get { details }.any {
                get { type }.isEqualTo(EventDetails.Bin)
                get { message }.isEqualTo("411111")
            }
            get { details }.any {
                get { type }.isEqualTo(EventDetails.Last4)
                get { message }.isEqualTo("1111")
            }
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
            get { isMaskSatisfied }.isTrue()
            get { isComplete }.isFalse()
            get { details }.any {
                get { type }.isEqualTo(EventDetails.CardBrand)
                get { message }.isEqualTo(CardBrands.VISA.label)
            }
            get { details }.any {
                get { type }.isEqualTo(EventDetails.Bin)
                get { message }.isEqualTo("424242")
            }
            get { details }.any {
                get { type }.isEqualTo(EventDetails.Last4)
                get { message }.isEqualTo("4243")
            }
        }
    }

    @Test
    fun `exposes card metadata for cards with satisfied masks`() {
        cardNumberElement.setText("5432 1098 7654 3210")

        expectThat(cardNumberElement) {
            get { cardMetadata }.isNotNull().and {
                get { brand }.isEqualTo(CardBrands.MASTERCARD.label)
                get { bin }.isEqualTo("543210")
                get { last4 }.isEqualTo("3210")
            }
            get { mask }.isEqualTo(ElementMask(CardBrandEnricher.CardMasks.MASK_4_8_12GAPS_16DIGITS))
            get { cvcMask }.isEqualTo(CardBrandEnricher.CvcMasks.THREE_DIGIT)
            get { isMaskSatisfied }.isTrue()
        }
    }

    @Test
    fun `exposes card metadata for cards with unsatisfied masks`() {
        cardNumberElement.setText("5432 1098 7654 321")

        expectThat(cardNumberElement) {
            get { cardMetadata }.isNotNull().and {
                get { brand }.isEqualTo(CardBrands.MASTERCARD.label)
                get { bin }.isNull()
                get { last4 }.isNull()
            }
            get { mask }.isEqualTo(ElementMask(CardBrandEnricher.CardMasks.MASK_4_8_12GAPS_16DIGITS))
            get { cvcMask }.isEqualTo(CardBrandEnricher.CvcMasks.THREE_DIGIT)
            get { isMaskSatisfied }.isFalse()
        }
    }

    @Test
    fun `exposes card metadata for partial cards`() {
        cardNumberElement.setText("4123")

        expectThat(cardNumberElement) {
            get { cardMetadata }.isNotNull().and {
                get { brand }.isEqualTo(CardBrands.VISA.label)
                get { bin }.isNull()
                get { last4 }.isNull()
            }
            get { mask }.isEqualTo(ElementMask(CardBrandEnricher.CardMasks.MASK_4_8_12GAPS_19DIGITS))
            get { cvcMask }.isEqualTo(CardBrandEnricher.CvcMasks.THREE_DIGIT)
            get { isMaskSatisfied }.isFalse()
        }
    }
}