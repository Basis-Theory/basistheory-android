package com.basistheory.android.view

import android.content.Context
import android.util.AttributeSet
import com.basistheory.android.view.transform.regexReplaceElementTransform
import com.basistheory.android.view.validation.luhnValidator

class CardVerificationCodeElement : TextElement {

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
        super.validator = { Regex("""^\d{3,4}$""").matches(it ?: "") }
    }

    companion object {
        private val digit = Regex("""\d""")

        val defaultMask: List<Any> =
            listOf(digit, digit, digit)
    }
}
