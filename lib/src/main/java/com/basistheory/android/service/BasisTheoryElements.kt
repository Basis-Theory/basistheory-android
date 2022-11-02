package com.basistheory.android.service

import com.basistheory.Configuration
import com.basistheory.TokenizeApi
import com.basistheory.android.view.TextElement
import com.basistheory.auth.ApiKeyAuth
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class BasisTheoryElements(
    private val apiUrl: String,
    private val apiKey: String,
    private val ioDispatcher: CoroutineDispatcher
) {
    private val apiClient =
        Configuration.getDefaultApiClient().also { client ->
            client.basePath = apiUrl

            (client.getAuthentication("ApiKey") as ApiKeyAuth).also { auth ->
                auth.apiKey = apiKey
            }
        }

    suspend fun tokenize(body: Any): Any {
        return withContext(ioDispatcher) {
            val tokenizeApi = TokenizeApi(apiClient)
            val requestMap = replaceElementRefs(toMap(body))
            tokenizeApi.tokenize(requestMap)
        }
    }

    private fun replaceElementRefs(map: MutableMap<String, Any?>): MutableMap<String, Any?> {
        for ((key, value) in map) {
            if (value == null) continue
            val fieldType = value::class.java
            if (!fieldType.isPrimitive && fieldType != String::class.java) {
                if (fieldType == TextElement::class.java) {
                    val element = value as TextElement
                    map[key] = element.getText()?.toString() as Any
                } else {
                    val children = toMap(value)
                    map[key] = children
                    replaceElementRefs(children)
                }
            }
        }

        return map
    }

    private fun toMap(value: Any): MutableMap<String, Any?> =
        value::class.java.declaredFields.associateBy(
            { it.name },
            {
                it.isAccessible = true
                it.get(value)
            }).toMutableMap()

    companion object {
        @JvmStatic
        fun builder(): BasisTheoryElementsBuilder = BasisTheoryElementsBuilder()
    }
}
