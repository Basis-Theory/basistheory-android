package com.basistheory.android.view

import android.content.Context
import android.util.AttributeSet
import com.basistheory.android.model.KeyboardType
import com.basistheory.android.view.mask.ElementMask
import com.basistheory.android.view.validation.RegexValidator

class CardVerificationCodeElement @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextElement(context, attrs, defStyleAttr) {

    var cardNumberElement: CardNumberElement? = null
        set(value) {
            field = value

            if (value != null && cardNumberElement != value) {
                super.mask =
                    cardNumberElement?.cardDetails?.cvcMask?.let { ElementMask(it) } ?: defaultMask
                field?.addChangeEventListener { updateMask() }
            }
        }

    init {
        super.keyboardType = KeyboardType.NUMBER
        super.mask = defaultMask
        super.validator = RegexValidator("""^\d{3,4}$""")
    }

    companion object {
        private val digit = Regex("""\d""")

        val defaultMask = ElementMask(
            listOf(digit, digit, digit)
        )
    }

    private fun updateMask() {
        super.mask = cardNumberElement?.cardDetails?.cvcMask?.let { ElementMask(it) } ?: defaultMask
    }
}
