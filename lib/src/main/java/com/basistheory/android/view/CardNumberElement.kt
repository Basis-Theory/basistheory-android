package com.basistheory.android.view

import android.content.Context
import android.util.AttributeSet
import com.basistheory.android.event.ChangeEvent
import com.basistheory.android.event.EventDetails
import com.basistheory.android.model.CardMetadata
import com.basistheory.android.model.KeyboardType
import com.basistheory.android.service.CardBrandEnricher
import com.basistheory.android.view.mask.ElementMask
import com.basistheory.android.view.transform.RegexReplaceElementTransform
import com.basistheory.android.view.validation.LuhnValidator

class CardNumberElement @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextElement(context, attrs, defStyleAttr) {

    private val cardBrandEnricher: CardBrandEnricher = CardBrandEnricher()

    init {
        super.keyboardType = KeyboardType.NUMBER
        super.mask = defaultMask
        super.transform = RegexReplaceElementTransform(Regex("""\s"""), "")
        super.validator = LuhnValidator()
    }

    var cardMetadata: CardMetadata? = null
        private set

    internal var cvcMask: String? = null

    override fun beforeTextChanged(value: String?): String? {
        val cardDigits = getDigitsOnly(value)
        val cardBrandDetails = cardBrandEnricher.evaluateCard(cardDigits)

        if (cardBrandDetails != null)
            mask = ElementMask(
                cardBrandDetails.cardMask,
                cardBrandDetails.validDigitCounts
                    .map { it + cardBrandDetails.gapCount }
                    .toIntArray()
            )

        val isMaskSatisfied = mask?.isSatisfied(value) ?: true

        cardMetadata = CardMetadata(
            cardBrandDetails?.brand,
            cardDigits?.take(cardDigits.binLength()).takeIf { isMaskSatisfied },
            cardDigits?.takeLast(4).takeIf { isMaskSatisfied },
        )
        cvcMask = cardBrandDetails?.cvcMask

        return value
    }

    override fun createElementChangeEvent(): ChangeEvent {
        val eventDetails = mutableListOf<EventDetails>()
        val value = getTransformedText()

        this.cardMetadata?.brand?.let {
            eventDetails.add(
                EventDetails(
                    EventDetails.CardBrand,
                    it
                )
            )
        }

        if (value != null && isMaskSatisfied) {
            eventDetails.add(
                EventDetails(
                    EventDetails.CardBin,
                    value.take(value.binLength())
                )
            )

            eventDetails.add(
                EventDetails(
                    EventDetails.CardLast4,
                    value.takeLast(4)
                )
            )
        }

        return ChangeEvent(
            isComplete,
            isEmpty,
            isValid,
            isMaskSatisfied,
            eventDetails
        )
    }

    private fun getDigitsOnly(text: String?): String? {
        val maskedValue = mask?.evaluate(text, inputAction)
        return transform?.apply(maskedValue) ?: maskedValue
    }

    private fun String.binLength() = if(this.length >= 16) 8 else 6

    companion object {
        private val digit = Regex("""\d""")

        val defaultMask = ElementMask(
            (1..19).map {
                if (it % 5 == 0 && it > 0) " " else digit
            }
        )
    }
}
