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

internal fun replaceElementRefs(value: Any?): Any? {
    if (value == null) return null
    val fieldType = value::class.java

    return if (fieldType.isPrimitiveType()) value
    else if (fieldType.isArray) {
        val array = value as Array<*>
        array.map {
            it?.let { arrayValue ->
                replaceElementRefs(arrayValue)
            }
        }
    } else {
        when (value) {
            is TextElement -> {
                value.tryGetTextToTokenize()
            }
            is ElementValueReference -> {
                value.getValue()
            }
            else -> {
                val children = if (value as? MutableMap<String, Any?> != null) value.toMutableMap() else value.toMap()
                for ((key, mapValue) in children) {
                    children[key] = replaceElementRefs(mapValue)
                }

                children
            }
        }
    }
}

internal fun TextElement.tryGetTextToTokenize(): String? {
    if (!this.isComplete)
        throw IncompleteElementException(this.id)

    return this.getTransformedText()
}

private fun String.toElementValueReference(): ElementValueReference =
    ElementValueReference { this }