package com.basistheory.android.util

import com.basistheory.android.model.ElementValueReference
import com.basistheory.android.model.exceptions.IncompleteElementException
import com.basistheory.android.view.TextElement

fun transformResponseToValueReferences(data: Any?): Any? =
    if (data == null) null
    else if (data::class.java.isPrimitiveType()) data.toString().toElementValueReference()
    else if (data::class.java.isArray) {
        (data as Array<*>).map { transformResponseToValueReferences(it) }
    } else {
        val map = (data as Map<*, *>).toMutableMap()
        map.forEach { (key, value) -> map[key] = transformResponseToValueReferences(value) }
        map
    }

internal fun replaceElementRefs(map: MutableMap<String, Any?>): MutableMap<String, Any?> {
    for ((key, value) in map) {
        if (value == null) continue
        val fieldType = value::class.java
        if (!fieldType.isPrimitiveType()) {
            when (value) {
                is TextElement -> {
                    map[key] = value.tryGetTextToTokenize()
                }
                is ElementValueReference -> {
                    map[key] = value.getValue()
                }
                else -> {
                    val children = value.toMap()
                    map[key] = children
                    replaceElementRefs(children)
                }
            }
        }
    }

    return map
}

internal fun TextElement.tryGetTextToTokenize(): String? {
    if (!this.isComplete)
        throw IncompleteElementException(this.id)

    return this.getTransformedText()
}

private fun String.toElementValueReference(): ElementValueReference =
    ElementValueReference { this }