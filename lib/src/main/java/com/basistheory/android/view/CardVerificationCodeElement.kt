package com.basistheory.android.view

import android.content.Context
import android.util.AttributeSet
import com.basistheory.android.model.KeyboardType

class CardVerificationCodeElement @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0) : TextElement(context, attrs, defStyleAttr) {

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
}
