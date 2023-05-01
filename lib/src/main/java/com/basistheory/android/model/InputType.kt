package com.basistheory.android.model

import android.text.InputType as AndroidInputType

enum class InputType(val androidInputType: Int, val isConcealed: Boolean = false) {
    TEXT(AndroidInputType.TYPE_CLASS_TEXT),
    NUMBER(AndroidInputType.TYPE_CLASS_NUMBER),
    TEXT_PASSWORD(AndroidInputType.TYPE_TEXT_VARIATION_PASSWORD, true),
    NUMBER_PASSWORD(AndroidInputType.TYPE_NUMBER_VARIATION_PASSWORD, true);

    companion object {
        fun fromInt(value: Int): InputType =
            values().firstOrNull { it.androidInputType == value }
                ?: throw TypeCastException("$value is not a valid InputType")
    }
}