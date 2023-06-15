package com.basistheory.android.util

import com.basistheory.android.model.ElementValueReference
import com.basistheory.android.model.exceptions.IncompleteElementException
import com.basistheory.android.view.TextElement
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

fun transformResponseToValueReferences(data: Any?): Any? =
    if (data == null) null
    else if (data::class.java.isPrimitiveType()) data.toString().toElementValueReference()
    else if (data::class.java.isArray) {
        (data as Array<*>).map { transformResponseToValueReferences(it) }
    } else if (data is Collection<*>) {
        data.map { transformResponseToValueReferences(it) }
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
            it?.let { item ->
                replaceElementRefs(item)
            }
        }
    } else if (value is Collection<*>) {
        value.map {
            it?.let { item ->
                replaceElementRefs(item)
            }
        }
    } else {
        when (value) {
            is TextElement -> value.tryGetTextToTokenize()
            is ElementValueReference -> value.getValue()
            else -> {
                val children =
                    if (value as? MutableMap<String, Any?> != null) value.toMutableMap() else value.toMap()
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


internal fun getElementsValues(body: Any) =
    body.let {
        when {
            it::class.java.isPrimitiveType() -> it.toString()
            it is TextElement -> it.getTransformedText()
            it is ElementValueReference -> it.getValue()
            else -> replaceElementRefs(it.toMap())
        }
    }

internal fun mapObjToRequestBody(contentType: String, obj: Any): RequestBody? =
    when (contentType) {
        "application/json" -> obj.toString().toRequestBody(contentType.toMediaType())
        "application/x-www-form-urlencoded" -> obj.let {
            convertObjectToFormUrlEncoded(it)
        }.let { encodeParamsToFormUrlEncoded(it) }.toRequestBody(
            contentType.toMediaType()
        )
        else -> throw IOException("Content-Type not supported")
    }

internal fun convertObjectToFormUrlEncoded(obj: Any?, prefix: String = ""): Map<String, String> {
    val formParams = mutableMapOf<String, String>()

    when (obj) {
        is Map<*, *> -> {
            for ((key, value) in obj.entries) {
                formParams.putAll(
                    convertObjectToFormUrlEncoded(
                        value,
                        if (prefix.isNotEmpty()) "$prefix[$key]" else key.toString()
                    )
                )
            }
        }

        is List<*> -> obj.withIndex().forEach { (index, value) ->
            formParams.putAll(
                convertObjectToFormUrlEncoded(
                    value,
                    if (prefix.isNotEmpty()) "$prefix[$index]" else index.toString()
                )
            )
        }

        else -> formParams[(if (prefix.isNotEmpty()) prefix else "")] = obj.toString()
    }

    return formParams
}

internal fun encodeParamsToFormUrlEncoded(formParams: Map<String, String>): String =
    formParams.entries.joinToString("&") { (key, value) ->

        val encodedKey = java.net.URLEncoder.encode(key, "UTF-8")

        val encodedValue =
            java.net.URLEncoder.encode(
                value,
                "UTF-8"
            )

        "${encodedKey}=${encodedValue}"
    }