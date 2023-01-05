package com.basistheory.android.view

import android.app.Activity
import android.media.metrics.Event
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
    fun `applies mask based on card brand`() {
        cardNumberElement.setText("4242424242424242")
        expectThat(cardNumberElement.mask)
            .isEqualTo(ElementMask(CardBrandEnricher.CardMasks.MASK_4_8_12GAPS_19LENGTH))

        cardNumberElement.setText("5555555555554444")
        expectThat(cardNumberElement.mask)
            .isEqualTo(ElementMask(CardBrandEnricher.CardMasks.MASK_4_8_12GAPS_16LENGTH))

        cardNumberElement.setText("371449635398431")
        expectThat(cardNumberElement.mask)
            .isEqualTo(ElementMask(CardBrandEnricher.CardMasks.MASK_4_10GAPS_15LENGTH))
    }

    @Test
    fun `applies the transform when retrieving the value`() {
        cardNumberElement.setText("4242 4242 4242 4242")
        expectThat(cardNumberElement.getText()).isEqualTo("4242424242424242")

        cardNumberElement.setText("4242-4242-4242-4242")
        expectThat(cardNumberElement.getText()).isEqualTo("4242424242424242")
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
            get { isComplete }.isTrue()
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
    fun `exposes card metadata for complete cards`() {
        cardNumberElement.setText("4123 4567 8901 2345")

        expectThat(cardNumberElement.cardMetadata).isNotNull().and {
            get { brand }.isEqualTo(CardBrands.VISA.label)
            get { cardMask }.isEqualTo(CardBrandEnricher.CardMasks.MASK_4_8_12GAPS_19LENGTH)
            get { cvcMask }.isEqualTo(CardBrandEnricher.CvcMasks.THREE_DIGIT)
            get { isComplete }.isTrue()
            get { bin }.isEqualTo("412345")
            get { last4 }.isEqualTo("2345")
        }
    }

    @Test
    fun `exposes card metadata for partial cards`() {
        cardNumberElement.setText("4123")

        expectThat(cardNumberElement.cardMetadata).isNotNull().and {
            get { brand }.isEqualTo(CardBrands.VISA.label)
            get { cardMask }.isEqualTo(CardBrandEnricher.CardMasks.MASK_4_8_12GAPS_19LENGTH)
            get { cvcMask }.isEqualTo(CardBrandEnricher.CvcMasks.THREE_DIGIT)
            get { isComplete }.isFalse()
            get { bin }.isNull()
            get { last4 }.isNull()
        }
    }
}