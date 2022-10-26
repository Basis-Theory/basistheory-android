package com.basistheory.android

import com.basistheory.ApiClient
import com.basistheory.Configuration
import com.basistheory.TokenizeApi
import com.basistheory.auth.ApiKeyAuth
import com.google.gson.Gson
import io.github.cdimascio.dotenv.dotenv

class BasisTheoryElements {
    companion object {
        private val dotenv = dotenv {
            directory = "/assets"
            filename = "env"
        }

        private val apiClient: ApiClient = Configuration.getDefaultApiClient().also { client ->
            client.basePath = dotenv["BASIS_THEORY_API_URL"] ?: "https://api.basistheory.com"

            (client.getAuthentication("ApiKey") as ApiKeyAuth).also { auth ->
                auth.apiKey = dotenv["BASIS_THEORY_API_KEY"]
            }
        }

//        fun tokenize(request: Map<String, Any>): Any {
//            val tokenizeApi = TokenizeApi(apiClient)
//            val mutatedRequest = replaceElementRefs(body)
////            val tokenizeRequest = replaceElementRefs(body, body::class.java)
//            val mapRequest = mapOf("type" to "token", "data" to "foo")
//            return tokenizeApi.tokenize(mapRequest)
//        }

        fun tokenize(body: Any): Any {
            val tokenizeApi = TokenizeApi(apiClient)
            val requestMap = replaceElementRefs(toMap(body))
            return tokenizeApi.tokenize(requestMap)
        }

//        fun <T>replaceElementRefs(body: Any, clazz: Class<T>): Any {
//            for (field in clazz.declaredFields) {
//                if (!field.type.isPrimitive && field.type != String::class.java) {
//                    if (field.type == TextElement::class.java) {
//                        // replace property with element value
//                        field.isAccessible = true
//                        var element = field.get(body) as TextElement
//                        field.set(body, element.getValue()?.toString())
//                    } else {
//                        field.isAccessible = true
//                        val value = field.get(body)
//                        replaceElementRefs(value, field.type)
//                    }
//                }
//            }
//
//            return body
//        }

        fun replaceElementRefs(map: MutableMap<String, Any?>): MutableMap<String, Any?> {
            for ((key, value) in map) {
                if (value == null) continue
                val fieldType = value::class.java
                if (!fieldType.isPrimitive && fieldType != String::class.java) {
                    if (fieldType == TextElement::class.java) {
                        val element = value as TextElement
                        map[key] = element.getValue()?.toString() as Any
                    } else {
                        val children = toMap(value)
                        replaceElementRefs(children)
                    }
                }
            }

            return map
        }

        fun toMap(value: Any): MutableMap<String, Any?> =
            value::class.java.declaredFields.associateBy(
                { it.name },
                {
                    it.isAccessible = true
                    it.get(value)
                }).toMutableMap()
    }
}