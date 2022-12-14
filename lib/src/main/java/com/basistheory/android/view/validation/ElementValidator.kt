package com.basistheory.android.view.validation

abstract class ElementValidator internal constructor() {
    abstract fun validate(value: String?): Boolean
}