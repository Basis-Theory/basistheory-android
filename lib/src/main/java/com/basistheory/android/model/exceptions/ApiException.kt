package com.basistheory.android.model.exceptions

class ApiException (
    val code: Int,
    val responseHeaders: Map<String, List<String>>?,
    val responseBody: String?,
    override val message: String?
): Exception(message)