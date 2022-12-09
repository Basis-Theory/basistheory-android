package com.basistheory.android.view.mask

import com.basistheory.android.model.InputAction

internal class Mask(mask: List<Any>) {

    private val sanitizedMask = sanitizeAndValidateMask(mask)

    fun evaluate(text: String?, action: InputAction): String? {
        if (text.isNullOrEmpty())
            return ""

        val source = text.iterator()
        val maskedValue = mutableListOf<Char>()
        var inputChar = source.nextOrNull()

        for (maskChar in sanitizedMask) {
            if (maskChar !is Regex && maskChar == inputChar.toString()) {
                maskedValue.add(maskChar.toString().single())
                // if text starts with maskedValue, then move the iterator forward
                if (text.toString().startsWith(maskedValue.joinToString("")))
                    inputChar = source.nextOrNull()
            } else if (maskChar is Regex) {
                inputChar = source.nextCharMatchingMask(inputChar, maskChar)

                if (inputChar == null) {
                    break // do not render placeholders
                } else if (!maskChar.matches(inputChar.toString())) {
                    inputChar = source.nextOrNull()
                } else {
                    maskedValue.add(inputChar)
                    inputChar = source.nextOrNull()
                }
            } else {
                if (inputChar == null && action == InputAction.DELETE) break
                maskedValue.add(maskChar.toString().single())
            }
        }

        return maskedValue.joinToString("")
    }

    fun isComplete(value: String?): Boolean =
        !value.isNullOrEmpty() && value.length == sanitizedMask.count()

    private fun sanitizeAndValidateMask(
        mask: List<Any>
    ): List<Any> = mask
        .ifEmpty { throw IllegalArgumentException("Invalid mask") }
        .map {
            when {
                it is Regex -> it
                (it is Char) || (it is String && it.length == 1) ->
                    when (it.toString()) {
                        "#" -> Regex("""\d""")
                        "x" -> Regex("[A-Za-z]")
                        "*" -> Regex(".")
                        else -> it
                    }
                else -> throw IllegalArgumentException("Invalid mask")
            }
        }
        .toList()
}

internal fun CharIterator.nextOrNull(): Char? = if (hasNext()) nextChar() else null

internal fun CharIterator.nextCharMatchingMask(inputChar: Char?, maskChar: Regex): Char? {
    var nextInputChar = inputChar ?: return null

    while (hasNext()) {
        if (maskChar.matches(nextInputChar.toString())) break
        nextInputChar = nextChar()
    }

    return if (maskChar.matches(nextInputChar.toString())) nextInputChar else null
}
