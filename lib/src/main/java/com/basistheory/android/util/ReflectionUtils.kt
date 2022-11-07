package com.basistheory.android.util

import org.apache.commons.lang3.ClassUtils

fun Any.toMap(): MutableMap<String, Any?> {
    require(!this::class.java.isPrimitiveType())

    return this::class.java.declaredFields.associateBy(
        { it.name },
        {
            it.isAccessible = true
            it.get(this)
        }).toMutableMap()
}


fun Class<*>.isPrimitiveType(): Boolean =
    ClassUtils.isPrimitiveOrWrapper(this) || this == String::class.java
