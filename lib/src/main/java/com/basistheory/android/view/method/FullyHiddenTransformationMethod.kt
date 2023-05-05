package com.basistheory.android.view.method

import android.text.method.PasswordTransformationMethod
import android.view.View

/**
 * Customizes PasswordTransformationMethod to also hide the current character while typing
 */
internal class FullyHiddenTransformationMethod : PasswordTransformationMethod() {

    override fun getTransformation(charSequence: CharSequence, view: View): CharSequence {
        return FullyHiddenCharSequence(charSequence)
    }

    private inner class FullyHiddenCharSequence(private val charSequence: CharSequence) : CharSequence {
        private val dot = '\u2022'

        override val length: Int
            get() = charSequence.length

        override fun get(index: Int): Char = dot

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence =
            FullyHiddenCharSequence(charSequence.subSequence(startIndex, endIndex))
    }
}