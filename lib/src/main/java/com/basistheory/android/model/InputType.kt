package com.basistheory.android.model

import android.text.InputType as AndroidInputType

enum class InputType(val attributeValue: Int, val androidInputType: Int, val isConcealed: Boolean = false) {
    TEXT(
        AndroidInputType.TYPE_CLASS_TEXT,
        AndroidInputType.TYPE_CLASS_TEXT
    ),
    NUMBER(
        AndroidInputType.TYPE_CLASS_NUMBER,
        AndroidInputType.TYPE_CLASS_NUMBER
    ),
    TEXT_PASSWORD(
        AndroidInputType.TYPE_CLASS_TEXT or AndroidInputType.TYPE_TEXT_VARIATION_PASSWORD,
        AndroidInputType.TYPE_TEXT_VARIATION_PASSWORD,
        true
    ),
    NUMBER_PASSWORD(
        AndroidInputType.TYPE_CLASS_NUMBER or AndroidInputType.TYPE_NUMBER_VARIATION_PASSWORD,
        AndroidInputType.TYPE_CLASS_NUMBER,
        true
    );

    companion object {
        fun fromAndroidAttr(androidAttrValue: Int): InputType =
            InputType.values().find { it.attributeValue == androidAttrValue }
                ?: throw IllegalArgumentException("Unknown input type '${androidAttrValue}'")
    }
}