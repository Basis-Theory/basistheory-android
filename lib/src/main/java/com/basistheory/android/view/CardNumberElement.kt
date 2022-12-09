package com.basistheory.android.view

import android.content.Context
import android.util.AttributeSet
import com.basistheory.android.event.ChangeEvent
import com.basistheory.android.event.EventDetails
import com.basistheory.android.service.CardBrandEnricher
import com.basistheory.android.view.mask.CardNumberMaskWatcher
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

    private fun init() {
        super.keyboardType = KeyboardType.NUMBER
        super.mask = defaultMask
        super.transform = regexReplaceElementTransform(Regex("""\s"""), "")
        super.validator = ::luhnValidator
        super.watcher = { mask -> mask?.let { CardNumberMaskWatcher(it) } }
        super.elementChangeEvent = { value: String?, isComplete: Boolean, isEmpty: Boolean, isValid: Boolean ->
            val cardResult = cardBrandEnricher.evaluateCard(value)
            val eventDetails = cardResult?.cardDetails?.brand?.let{ b -> mutableListOf( EventDetails("cardBrand", b)) } ?: mutableListOf()

            ChangeEvent(
                isComplete,
                isEmpty,
                isValid,
                eventDetails)
        }

        cardBrandEnricher = CardBrandEnricher()
    }

    companion object {
        private val digit = Regex("""\d""")

        val defaultMask: List<Any> =
            (1..19).map {
                if (it % 5 == 0 && it > 0) " " else digit
            }
    }
}
