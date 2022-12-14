package com.basistheory.android.model

import android.text.InputType

enum class KeyboardType(val inputType: Int) {
    TEXT(InputType.TYPE_CLASS_TEXT),
    NUMBER(InputType.TYPE_CLASS_NUMBER);

    companion object {
        fun fromInt(value: Int): KeyboardType =
            values().firstOrNull { it.inputType == value }
                ?: throw TypeCastException("$value is not valid KeyboardType")
    }
}