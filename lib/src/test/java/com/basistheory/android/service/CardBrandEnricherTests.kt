package com.basistheory.android.service

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isNull
import strikt.assertions.isTrue

@RunWith(JUnitParamsRunner::class)
class CardBrandEnricherTests {

    private val cardBrandEnricher = CardBrandEnricher()

    @Test
    @Parameters(
        value = [
            "4242424242424242, visa, ${CardBrandEnricher.CardMasks.MASK_4_8_12GAPS_19LENGTH}",
            "5555555555555555, mastercard, ${CardBrandEnricher.CardMasks.MASK_4_8_12GAPS_16LENGTH}",
            "378282246310005, americanExpress, ${CardBrandEnricher.CardMasks.MASK_4_10GAPS_15LENGTH}",
            "36227206271667, dinersClub, ${CardBrandEnricher.CardMasks.MASK_4_10GAPS_19LENGTH}",
            "6011000990139424, discover, ${CardBrandEnricher.CardMasks.MASK_4_8_12GAPS_19LENGTH}",
            "3566495867324859, jcb, ${CardBrandEnricher.CardMasks.MASK_4_8_12GAPS_19LENGTH}",
            "620000000000000, unionPay, ${CardBrandEnricher.CardMasks.MASK_4_8_12GAPS_19LENGTH}",
            "673948657439, maestro, ${CardBrandEnricher.CardMasks.MASK_4_8_12GAPS_19LENGTH}",
            "6277803485967438, elo, ${CardBrandEnricher.CardMasks.MASK_4_8_12GAPS_16LENGTH}",
            "2201003122003222, mir, ${CardBrandEnricher.CardMasks.MASK_4_8_12GAPS_19LENGTH}",
            "6374335843768548, hiper, ${CardBrandEnricher.CardMasks.MASK_4_8_12GAPS_16LENGTH}",
            "6062824384829392, hipercard, ${CardBrandEnricher.CardMasks.MASK_4_8_12GAPS_16LENGTH}"
        ]
    )
    fun `should identify correct card brand and mask`(
        cardNumber: String,
        expectedBrand: String,
        expectedCardMask: String
    ) {
        with(cardBrandEnricher.evaluateCard(cardNumber)) {
            expectThat(this?.brand).isEqualTo(expectedBrand)
            expectThat(this?.cardMask).isEqualTo(expectedCardMask)
        }
    }

    @Test
    fun `should pick best match based on identifier length`() {
        expectThat(cardBrandEnricher.evaluateCard("4011784867543859")?.brand).isEqualTo(
            "elo"
        )
    }

    @Test
    fun `should set complete for best match card lengths`() {
        // discover valid lengths are 16 or 19
        val sixteenDigitsDiscoverCardNumber = "6582937163058334"

        expectThat(cardBrandEnricher.evaluateCard(sixteenDigitsDiscoverCardNumber)?.complete).isTrue()
        expectThat(cardBrandEnricher.evaluateCard("${sixteenDigitsDiscoverCardNumber}1")?.complete).isFalse()
        expectThat(cardBrandEnricher.evaluateCard("${sixteenDigitsDiscoverCardNumber}12")?.complete).isFalse()
        expectThat(cardBrandEnricher.evaluateCard("${sixteenDigitsDiscoverCardNumber}123")?.complete).isTrue()
    }

    @Test
    fun `should handle null or empty card number`() {
        val nullCard = cardBrandEnricher.evaluateCard(null)
        expectThat(nullCard).isNull()

        val emptyCard = cardBrandEnricher.evaluateCard("")
        expectThat(emptyCard).isNull()
    }
}