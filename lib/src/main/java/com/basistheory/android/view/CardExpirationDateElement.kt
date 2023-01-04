package com.basistheory.android.view

import android.content.Context
import android.util.AttributeSet
import com.basistheory.android.model.ElementValueReference
import com.basistheory.android.model.KeyboardType
import com.basistheory.android.view.mask.ElementMask
import com.basistheory.android.view.validation.FutureDateValidator

class CardExpirationDateElement @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0) : TextElement(context, attrs, defStyleAttr) {

    fun month(): ElementValueReference = ElementValueReference(::getMonthValue)

    fun year(): ElementValueReference = ElementValueReference(::getYearValue)

    init {
        super.keyboardType = KeyboardType.NUMBER
        super.mask = defaultMask
        super.validator = FutureDateValidator()
    }

    /**
     * If the user entered a leading digit > 1, auto insert a leading 0
     */
    override fun beforeTextChanged(value: String?): String? {
        val firstChar = value?.firstOrNull()

        if (firstChar?.isDigit() != true) return value

        val firstDigit = firstChar.digitToInt()
        return if (firstDigit > 1) "0$value" else value
    }

    private fun getMonthValue(): String? =
        getTransformedText()
            ?.split("/")
            ?.elementAtOrNull(0)

    private fun getYearValue(): String? =
        getTransformedText()
            ?.split("/")
            ?.elementAtOrNull(1)
            ?.let { "20$it" }

    companion object {
        private val digit = Regex("""\d""")

        val defaultMask = ElementMask(
            listOf(digit, digit, "/", digit, digit)
        )
    }
}
