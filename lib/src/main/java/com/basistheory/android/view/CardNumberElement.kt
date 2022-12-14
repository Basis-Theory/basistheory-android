package com.basistheory.android.view

import android.content.Context
import android.util.AttributeSet
import com.basistheory.android.event.ChangeEvent
import com.basistheory.android.event.EventDetails
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

    var cardMetadata: CardBrandEnricher.CardMetadata? = null
        private set

    private val cardBrandEnricher: CardBrandEnricher = CardBrandEnricher()

    init {
        super.keyboardType = KeyboardType.NUMBER
        super.mask = defaultMask
        super.transform = RegexReplaceElementTransform(Regex("""\s"""), "")
        super.validator = LuhnValidator()
    }

    override fun beforeTextChanged(value: String?): String? {
        cardMetadata = cardBrandEnricher.evaluateCard(getDigitsOnly(value))

        if (cardMetadata?.cardMask != null)
            mask = ElementMask(cardMetadata!!.cardMask!!)

        return value
    }

    override fun createElementChangeEvent(
        value: String?,
        isComplete: Boolean,
        isEmpty: Boolean,
        isValid: Boolean
    ): ChangeEvent {
        val cardMetadata = cardBrandEnricher.evaluateCard(getDigitsOnly(value))
        this.cardMetadata = cardMetadata

        val eventDetails = this.cardMetadata?.brand?.let { brand ->
            mutableListOf(
                EventDetails(
                    "cardBrand",
                    brand
                )
            )
        } ?: mutableListOf()

        return ChangeEvent(
            cardMetadata?.complete ?: false,
            isEmpty,
            isValid,
            eventDetails
        )
    }

    private fun getDigitsOnly(text: String?): String? {
        val maskedValue = mask?.evaluate(text, inputAction)
        return transform?.apply(maskedValue) ?: maskedValue
    }

    companion object {
        private val digit = Regex("""\d""")

        val defaultMask = ElementMask(
            (1..19).map {
                if (it % 5 == 0 && it > 0) " " else digit
            }
        )
    }
}
