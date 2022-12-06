package com.basistheory.android.view

import android.content.Context
import android.util.AttributeSet
import com.basistheory.android.model.ElementValueReference
import com.basistheory.android.model.KeyboardType
import com.basistheory.android.view.transform.regexReplaceElementTransform
import com.basistheory.android.view.validation.futureDateValidator

class CardExpirationDateElement : TextElement {

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

    fun month(): ElementValueReference = ElementValueReference(::getMonthValue)

    fun year(): ElementValueReference = ElementValueReference(::getYearValue)

    private fun init() {
        super.keyboardType = KeyboardType.NUMBER
        super.mask = defaultMask
        super.validator = { futureDateValidator(it) }
    }

    private fun getMonthValue(): String? =
        getText()
            ?.split("/")
            ?.elementAtOrNull(0)

    private fun getYearValue(): String? =
        getText()
            ?.split("/")
            ?.elementAtOrNull(1)

    companion object {
        private val digit = Regex("""\d""")

        val defaultMask: List<Any> =
            listOf(digit, digit, "/", digit, digit)
    }

    // todo: support leading non-zero digits being entered by user and pre-pending 0 on month
}
