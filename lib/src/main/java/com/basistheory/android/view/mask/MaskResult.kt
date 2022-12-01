package com.basistheory.android.view.mask

import android.text.Editable

internal data class MaskResult(
    val maskedValue: String,
    val unMaskedValue: String,
    val isComplete: Boolean
) {
    internal fun apply(text: Editable) {
        val filters = text.filters
        text.filters = emptyArray()
        text.replace(0, text.length, maskedValue)
        text.filters = filters
    }
}

