package com.basistheory.android.view

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import com.basistheory.android.model.ElementValueReference
import com.basistheory.android.model.KeyboardType
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
        super.validate = { futureDateValidator(it) }
    }

    private fun getMonthValue(): String? =
        getText()
            ?.split("/")
            ?.elementAtOrNull(0)

    private fun getYearValue(): String? =
        getText()
            ?.split("/")
            ?.elementAtOrNull(1)
            ?.let { "20$it" }

    /**
     * If the user entered a leading digit > 1, auto insert a leading 0
     */
    override fun transformUserInput(userInput: String?): String? {
        val firstChar = userInput?.firstOrNull()

        if (firstChar?.isDigit() != true) return userInput

        val firstDigit = firstChar.digitToInt()
        return if (firstDigit > 1) "0$userInput" else userInput
    }

    companion object {
        private val digit = Regex("""\d""")

        val defaultMask: List<Any> =
            listOf(digit, digit, "/", digit, digit)
    }
}
