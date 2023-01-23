package com.basistheory.android.util

import com.basistheory.android.model.ElementValueReference

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

private fun String.toElementValueReference(): ElementValueReference =
    ElementValueReference { this }