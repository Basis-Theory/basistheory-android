package com.basistheory.android.view

import android.content.Context
import android.util.AttributeSet
import com.basistheory.android.model.InputType
import com.basistheory.android.view.mask.ElementMask
import com.basistheory.android.view.validation.RegexValidator

class CardVerificationCodeElement @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextElement(context, attrs, defStyleAttr) {

    var cardNumberElement: CardNumberElement? = null
        set(value) {
            if (value != null && cardNumberElement !== value) {
                field = value
                onCardNumberChanged(true)
                field?.addChangeEventListener { onCardNumberChanged() }
            } else {
                field = value
            }
        }

    init {
        super.inputType = InputType.NUMBER
        super.mask = defaultMask
        super.validator = validatorForLength(defaultMask.validLengths.max())
    }

    companion object {
        private val digit = Regex("""\d""")

        val defaultMask = ElementMask(
            listOf(digit, digit, digit)
        )

        fun validatorForLength(length: Int) =
            RegexValidator("""^\d{$length}$""")
    }

    private fun onCardNumberChanged(isInitialConfiguration: Boolean = false) {
        val oldMaskLength = super.mask?.validLengths?.max()

        val updatedMask = cardNumberElement
            ?.cvcMask
            ?.let { ElementMask(it) }
            ?: defaultMask

        super.mask = updatedMask
        super.validator = validatorForLength(updatedMask.validLengths.max())

        val updatedMaskLength = super.mask?.validLengths?.max()

        // publish a change event if the mask length changed
        if (!isInitialConfiguration && oldMaskLength != updatedMaskLength)
            super.publishChangeEvent()
    }
}
