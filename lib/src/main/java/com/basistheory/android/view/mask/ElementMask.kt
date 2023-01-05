package com.basistheory.android.view.mask

import com.basistheory.android.model.InputAction

class ElementMask {
    val characterMasks: List<Any>

    constructor(value: List<Any>) {
        characterMasks = sanitizeAndValidateMask(value)
    }

    constructor(value: String) {
        characterMasks = sanitizeAndValidateMask(value
            .split("")
            .filter { it.isNotEmpty() }
        )
    }

    val length: Int
        get() = characterMasks.size

    internal fun evaluate(text: String?, action: InputAction): String? {
        if (text.isNullOrEmpty())
            return ""

        val source = text.iterator()
        val maskedValue = mutableListOf<Char>()
        var inputChar = source.nextOrNull()

        for (charMask in characterMasks) {
            if (charMask !is Regex && charMask == inputChar.toString()) {
                maskedValue.add(charMask.toString().single())
                // if text starts with maskedValue, then move the iterator forward
                if (text.toString().startsWith(maskedValue.joinToString("")))
                    inputChar = source.nextOrNull()
            } else if (charMask is Regex) {
                inputChar = source.nextCharMatchingMask(inputChar, charMask)

                if (inputChar == null) {
                    break // do not render placeholders
                } else if (!charMask.matches(inputChar.toString())) {
                    inputChar = source.nextOrNull()
                } else {
                    maskedValue.add(inputChar)
                    inputChar = source.nextOrNull()
                }
            } else {
                if (inputChar == null && action == InputAction.DELETE) break
                maskedValue.add(charMask.toString().single())
            }
        }

        return maskedValue.joinToString("")
    }

    fun isComplete(value: String?): Boolean =
        !value.isNullOrEmpty() && value.length == characterMasks.count()

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

    private fun CharIterator.nextOrNull(): Char? = if (hasNext()) nextChar() else null

    private fun CharIterator.nextCharMatchingMask(inputChar: Char?, maskChar: Regex): Char? {
        var nextInputChar = inputChar ?: return null

        while (hasNext()) {
            if (maskChar.matches(nextInputChar.toString())) break
            nextInputChar = nextChar()
        }

        return if (maskChar.matches(nextInputChar.toString())) nextInputChar else null
    }

    override fun equals(other: Any?): Boolean =
        (other is ElementMask) && ElementMaskComparer(this) == ElementMaskComparer(other)

    override fun hashCode(): Int =
        ElementMaskComparer(this).hashCode()

    /**
     * Utility class to assist in comparing two ElementMask instances since
     * Regex objects are not comparable within the list of character masks
     */
    private data class ElementMaskComparer(val characterMaskPatterns: List<String>) {
        constructor(mask: ElementMask) : this(mask.toComparablePatterns())

        companion object {
            private fun ElementMask.toComparablePatterns(): List<String> =
                this.characterMasks.map {
                    when (it) {
                        is Regex -> it.pattern
                        else -> it.toString()
                    }
                }
        }
    }
}


