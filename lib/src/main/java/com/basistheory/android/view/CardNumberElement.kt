package com.basistheory.android.view

import android.content.Context
import android.util.AttributeSet
import com.basistheory.android.event.ChangeEvent
import com.basistheory.android.event.EventDetails
import com.basistheory.android.model.KeyboardType
import com.basistheory.android.service.CardBrandEnricher
import com.basistheory.android.view.transform.regexReplaceElementTransform
import com.basistheory.android.view.validation.luhnValidator

class CardNumberElement @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0) : TextElement(context, attrs, defStyleAttr) {

    var cardDetails: CardBrandEnricher.CardDetails? = null

    private val cardBrandEnricher: CardBrandEnricher = CardBrandEnricher()

    init {
        super.keyboardType = KeyboardType.NUMBER
        super.mask = defaultMask
        super.transform = regexReplaceElementTransform(Regex("""\s"""), "")
        super.validate = ::luhnValidator
    }

    override fun beforeTextChanged(value: String?): String? {
        val cardResult = cardBrandEnricher.evaluateCard(getDigitsOnly(value))
        cardDetails = cardResult.cardDetails

        if (cardResult.cardDetails?.cardMask != null)
            mask = cardResult.cardDetails!!.cardMask.toList()

        return value
    }

    override fun createElementChangeEvent(
        value: String?,
        isComplete: Boolean,
        isEmpty: Boolean,
        isValid: Boolean
    ): ChangeEvent {
        val cardResult = cardBrandEnricher.evaluateCard(getDigitsOnly(value))
        cardDetails = cardResult.cardDetails

        val eventDetails = cardResult.cardDetails?.brand?.let { brand ->
            mutableListOf(
                EventDetails(
                    "cardBrand",
                    brand
                )
            )
        } ?: mutableListOf()

        return ChangeEvent(
            cardResult.complete,
            isEmpty,
            isValid,
            eventDetails
        )
    }

    private fun getDigitsOnly(text: String?): String? {
        val maskedValue = maskValue?.evaluate(text, inputAction)
        return transform(maskedValue)
    }

    companion object {
        private val digit = Regex("""\d""")

        val defaultMask: List<Any> =
            (1..19).map {
                if (it % 5 == 0 && it > 0) " " else digit
            }
    }
}
