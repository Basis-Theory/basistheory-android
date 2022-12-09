package com.basistheory.android.view

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import com.basistheory.android.event.ChangeEvent
import com.basistheory.android.event.EventDetails
import com.basistheory.android.service.CardBrandEnricher
import com.basistheory.android.model.KeyboardType
import com.basistheory.android.view.transform.regexReplaceElementTransform
import com.basistheory.android.view.validation.luhnValidator

class CardNumberElement : TextElement {

    private lateinit var cardBrandEnricher: CardBrandEnricher

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun afterTextChangedHandler(editable: Editable?) {
        val cardResult = cardBrandEnricher.evaluateCard(getDigitsOnly(editable?.toString()))
        if (cardResult.cardDetails?.cardMask != null)
            mask = cardResult.cardDetails!!.cardMask.toList()

        super.afterTextChangedHandler(editable)
    }

    private fun init() {
        super.keyboardType = KeyboardType.NUMBER
        super.mask = defaultMask
        super.transform = regexReplaceElementTransform(Regex("""\s"""), "")
        super.validate = ::luhnValidator

        super.elementChangeEvent =
            { value: String?, _: Boolean, isEmpty: Boolean, isValid: Boolean ->
                val cardResult = cardBrandEnricher.evaluateCard(getDigitsOnly(value))
                val eventDetails = cardResult.cardDetails?.brand?.let { b ->
                    mutableListOf(
                        EventDetails(
                            "cardBrand",
                            b
                        )
                    )
                } ?: mutableListOf()

                ChangeEvent(
                    cardResult.complete ?: false,
                    isEmpty,
                    isValid,
                    eventDetails
                )
            }

        cardBrandEnricher = CardBrandEnricher()
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
