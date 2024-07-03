package com.basistheory.android.view

import android.content.Context
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import androidx.appcompat.widget.AppCompatEditText
import com.basistheory.android.BuildConfig

internal class SensitiveEditText(
    context: Context
) : AppCompatEditText(context, null, androidx.appcompat.R.attr.editTextStyle) {

    var allowTextAccess: Boolean = false
        internal set


    override fun addTextChangedListener(watcher: TextWatcher?) {
        if (allowTextAccess || BuildConfig.DEBUG) {
            super.addTextChangedListener(watcher)
        }
    }

    override fun getText(): Editable? {
        return if (allowTextAccess || BuildConfig.DEBUG)
            super.getText()
        else SpannableStringBuilder()
    }
}