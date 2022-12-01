package com.basistheory.android.view.mask

internal class Mask(
    private val mask: List<Any>,
) {
    private val maskPattern =
        convertMaskPatternToString(
            mask,
            "_"
        )

    private val placeholder = maskPattern.getMostRepeatedChar()

    fun apply(text: CharSequence, action: Action): MaskResult {
        val source = text.iterator()
        val maskedValue = mutableListOf<Char>()
        val unMaskedValue = mutableListOf<Char>()
        var currentChar = source.nextOrNull()

        loop@ for ((index, value) in maskPattern.withIndex()) {
            if (value == currentChar && value != placeholder) {
                maskedValue.add(value)
                currentChar = source.nextOrNull()
            } else if (value == placeholder) {
                currentChar = source.nextMaskChar(currentChar)
                val originalValue = mask[index]
                if (currentChar == null) {
                    break@loop // do not render placeholders
                } else if (originalValue is Regex && !originalValue.matches(currentChar.toString())) {
                    break@loop
                } else {
                    maskedValue.add(currentChar)
                    unMaskedValue.add(currentChar)
                    currentChar = source.nextOrNull()
                }
            } else {
                if (currentChar == null && action == Action.DELETE) break@loop
                maskedValue.add(value)
            }
        }

        var maskedText = maskedValue.joinToString("")
        var unMaskedText = unMaskedValue.joinToString("")
        val isComplete = maskedText.length == maskPattern.length && !maskedText.contains(placeholder)

        if (maskPattern.startsWith(maskedText)) {
            maskedText = ""
            unMaskedText = ""
        }

        return MaskResult(
            maskedText, unMaskedText, isComplete
        )
    }

    private fun convertMaskPatternToString(
        mask: List<Any>,
        placeholderChar: String
    ): String = mask.map { if (it is Regex) placeholderChar else it }.joinToString("")
}

internal fun CharIterator.nextOrNull(): Char? = if (hasNext()) nextChar() else null

internal fun CharIterator.nextMaskChar(char: Char?): Char? {
    var nextMaskChar = char ?: return null

    while (hasNext()) {
        if (nextMaskChar.isLetterOrDigit()) break
        nextMaskChar = nextChar()
    }

    return if (nextMaskChar.isLetterOrDigit()) nextMaskChar else null
}

internal fun String.getMostRepeatedChar(): Char = groupBy { it }.maxBy { it.value.size }.key
