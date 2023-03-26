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
    defStyleAttr: Int = 0
) : TextElement(context, attrs, defStyleAttr) {

    fun month(): ElementValueReference = ElementValueReference(this, ::getMonthValue)

    fun year(): ElementValueReference = ElementValueReference(this, ::getYearValue)

    fun setValueRef(
        monthRef: ElementValueReference,
        yearRef: ElementValueReference
    ) {
        val month = monthRef.getValue().toIntString()
        val year = yearRef.getValue().toIntString()

        setText("${month}${year?.takeLast(2)}")
    }

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
        val secondChar = value?.getOrNull(1)

        if (firstChar?.isDigit() != true || secondChar != null && !secondChar.isDigit()) return value

        val firstDigit = firstChar.digitToInt()

        val paddedValue = if (firstDigit > 1) "0$value" else value
        val month = paddedValue.take(2).toInt()

        //if (paddedValue[0].digitToInt() > 1) return ""

        return if (month < 1 || month > 12)
            "${paddedValue.firstOrNull()}${paddedValue.takeLast(paddedValue.length - month.toString().length)}}"
        else paddedValue
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

    private fun String?.toIntString(): String? =
        try {
            (this?.toInt()).toString()
        } catch (e: java.lang.NumberFormatException) {
            try {
                (this?.toDouble()?.toInt()).toString()
            } catch (e: java.lang.NumberFormatException) {
                this
            }
        }

    companion object {
        private val digit = Regex("""\d""")

        val defaultMask = ElementMask(
            listOf(digit, digit, "/", digit, digit)
        )
    }
}
