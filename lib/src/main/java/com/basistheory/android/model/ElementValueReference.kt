package com.basistheory.android.model

class ElementValueReference(private val valueGetter: () -> String?) {
    internal fun getValue(): String? = valueGetter()
}