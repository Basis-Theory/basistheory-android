package com.basistheory.android.view.mask

import android.text.Editable
import android.text.TextWatcher

internal open class MaskWatcher(mask: List<Any>) : TextWatcher {
    protected var mask: Mask = Mask(mask)
    protected var result: MaskResult? = null
    protected var isApplyingMask: Boolean = false

    val isMaskApplied: Boolean
        get() = isApplyingMask

    val maskedValue: String
        get() = result?.maskedValue.orEmpty()

    val unMaskedValue: String
        get() = result?.unMaskedValue.orEmpty()

    val isComplete: Boolean
        get() = result?.isComplete ?: false

    override fun afterTextChanged(editable: Editable?) {
        if (isApplyingMask || editable.isNullOrEmpty()) return

        isApplyingMask = true
        result?.apply(editable)
        isApplyingMask = false
    }

    override fun beforeTextChanged(
        charSequence: CharSequence?,
        start: Int,
        count: Int,
        after: Int
    ) {
    }

    override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
        if (isApplyingMask || charSequence.isNullOrEmpty()) return

        val action = if (before > 0 && count == 0) Action.DELETE else Action.INSERT

        result = mask.apply(charSequence, action)
    }
}


internal enum class Action {
    INSERT,
    DELETE
}