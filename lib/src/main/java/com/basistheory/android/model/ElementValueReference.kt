package com.basistheory.android.model

import com.basistheory.android.model.exceptions.IncompleteElementException
import com.basistheory.android.view.TextElement

class ElementValueReference(private val valueGetter: () -> String?) {
    private var _element: TextElement? = null

    constructor(element: TextElement, valueGetter: () -> String?) : this(valueGetter) {
        _element = element
    }

    internal fun getValue(): String? {
        if (_element != null && _element?.isComplete == false)
            throw IncompleteElementException(_element?.id ?: -1)

        return valueGetter()
    }
}