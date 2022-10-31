package com.basistheory.android

import com.basistheory.ApiClient
import com.basistheory.Configuration
import com.basistheory.TokenizeApi
import com.basistheory.auth.ApiKeyAuth

class BasisTheoryElements {
    companion object {

        fun tokenize(body: Any, apiKey: String): Any {
            val tokenizeApi = TokenizeApi(getApiClient(apiKey))
            val requestMap = replaceElementRefs(toMap(body))
            return tokenizeApi.tokenize(requestMap)
        }

        private fun getApiClient(apiKey: String): ApiClient =
            Configuration.getDefaultApiClient().also { client ->
                client.basePath = "https://api-dev.basistheory.com"

                (client.getAuthentication("ApiKey") as ApiKeyAuth).also { auth ->
                    auth.apiKey = apiKey
                }
            }

        private fun replaceElementRefs(map: MutableMap<String, Any?>): MutableMap<String, Any?> {
            for ((key, value) in map) {
                if (value == null) continue
                val fieldType = value::class.java
                if (!fieldType.isPrimitive && fieldType != String::class.java) {
                    if (fieldType == TextElement::class.java) {
                        val element = value as TextElement
                        map[key] = element.getValue()?.toString() as Any
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
    }
}