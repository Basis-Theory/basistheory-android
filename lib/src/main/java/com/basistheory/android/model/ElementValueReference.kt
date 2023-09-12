package com.basistheory.android.model

import com.basistheory.android.constants.ElementValueType
import com.basistheory.android.model.exceptions.IncompleteElementException
import com.basistheory.android.view.TextElement

class ElementValueReference(private val valueGetter: () -> String?) {
    private var _element: TextElement? = null
    private var _getValueType: ElementValueType = ElementValueType.STRING;

    constructor(
        element: TextElement,
        valueGetter: () -> String?,
        getValueType: ElementValueType?
    ) : this(valueGetter) {
        _element = element
        _getValueType = getValueType ?: ElementValueType.STRING
    }

    internal fun getValue(): String? {
        if (_element != null && _element?.isComplete == false)
            throw IncompleteElementException(_element?.id ?: -1)

        return valueGetter()
    }

    var getValueType: ElementValueType = ElementValueType.STRING
        get() = _getValueType
}