package com.basistheory.android.view.transform

abstract class ElementTransform internal constructor() {
    abstract fun apply(value: String?): String?
}