package com.basistheory.android.service

import com.basistheory.android.util.isPrimitiveType
import com.basistheory.android.util.toMap
import com.basistheory.android.view.TextElement
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BasisTheoryElements internal constructor(
    private val apiClientProvider: ApiClientProvider,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    @JvmOverloads
    suspend fun tokenize(body: Any, apiKeyOverride: String? = null): Any =
        withContext(ioDispatcher) {
            val tokenizeApiClient = apiClientProvider.getTokenizeApi(apiKeyOverride)
            val request =
                if (body::class.java.isPrimitiveType()) body
                else if (body is TextElement) body.getText()
                else replaceElementRefs(body.toMap())

            tokenizeApiClient.tokenize(request)
        }

    private fun replaceElementRefs(map: MutableMap<String, Any?>): MutableMap<String, Any?> {
        for ((key, value) in map) {
            if (value == null) continue
            val fieldType = value::class.java
            if (!fieldType.isPrimitiveType()) {
                if (value is TextElement) {
                    map[key] = value.getText()
                } else {
                    val children = value.toMap()
                    map[key] = children
                    replaceElementRefs(children)
                }
            }
        }

        return map
    }

    companion object {
        @JvmStatic
        fun builder(): BasisTheoryElementsBuilder = BasisTheoryElementsBuilder()
    }
}
