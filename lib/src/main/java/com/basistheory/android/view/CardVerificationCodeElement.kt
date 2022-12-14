package com.basistheory.android.view

import android.content.Context
import android.util.AttributeSet
import com.basistheory.android.model.KeyboardType

class CardVerificationCodeElement @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0) : TextElement(context, attrs, defStyleAttr) {

    var cardNumberElement: CardNumberElement? = null
        set(value) {
            field = value

            if (value != null && cardNumberElement != value) {
                super.mask = field?.cardDetails?.cvcMask?.toList() ?: defaultMask
                field?.addChangeEventListener { updateMask() }
            }
        }

    init {
        super.keyboardType = KeyboardType.NUMBER
        super.mask = defaultMask
        super.validate = { Regex("""^\d{3,4}$""").matches(it ?: "") }
    }

    companion object {
        private val digit = Regex("""\d""")

        val defaultMask: List<Any> =
            listOf(digit, digit, digit)
    }

    private fun updateMask() {
        super.mask = cardNumberElement?.cardDetails?.cvcMask?.toList() ?: defaultMask
    }
}
