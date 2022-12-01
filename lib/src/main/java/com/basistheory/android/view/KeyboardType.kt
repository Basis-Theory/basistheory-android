package com.basistheory.android.view

import android.text.InputType

enum class KeyboardType(val inputType: Int) {
    TEXT(InputType.TYPE_CLASS_TEXT),
    NUMBER(InputType.TYPE_CLASS_NUMBER),
    DATETIME(InputType.TYPE_CLASS_DATETIME);

    companion object {
        fun fromInt(value: Int): KeyboardType =
            values().firstOrNull { it.inputType == value }
                ?: throw TypeCastException("$value is not valid KeyboardType")
    }
}