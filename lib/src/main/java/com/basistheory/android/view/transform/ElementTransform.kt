package com.basistheory.android.view.transform

// open for extension internally within this package, but not by outside implementors
open class ElementTransform internal constructor() {
    open fun apply(value: String?): String? {
        return value
    }
}