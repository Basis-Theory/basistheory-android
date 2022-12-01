package com.basistheory.android.view.mask

import android.text.Editable
import android.text.TextWatcher

internal class MaskWatcher(mask: List<Any>) : TextWatcher {
    private val mask: Mask = Mask(mask)

    private var selfChange: Boolean = false
    private var result: MaskResult? = null

    val maskedValue: String
        get() = result?.maskedValue.orEmpty()

    val unMaskedValue: String
        get() = result?.unMaskedValue.orEmpty()

    val isComplete: Boolean
        get() = result?.isComplete ?: false

    override fun afterTextChanged(editable: Editable?) {
        if (selfChange || editable.isNullOrEmpty()) return

        selfChange = true
        result?.apply(editable)
        selfChange = false
    }

    override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
        if (selfChange || charSequence.isNullOrEmpty()) return

        val action = if (before > 0 && count == 0) Action.DELETE else Action.INSERT

        result = mask.apply(charSequence, action)
    }
}


internal enum class Action {
    INSERT,
    DELETE
}