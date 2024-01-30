package com.basistheory.android.service

import com.basistheory.ApiClient
import com.basistheory.android.model.ElementValueReference
import com.basistheory.android.util.isPrimitiveType
import com.basistheory.android.util.replaceElementRefs
import com.basistheory.android.util.toMap
import com.basistheory.android.util.transformResponseToValueReferences
import com.basistheory.android.view.TextElement
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.reflect.Type

interface Proxy {

    suspend fun get(proxyRequest: ProxyRequest, apiKeyOverride: String? = null): Any?

    suspend fun post(proxyRequest: ProxyRequest, apiKeyOverride: String? = null): Any?

    suspend fun put(proxyRequest: ProxyRequest, apiKeyOverride: String? = null): Any?

    suspend fun patch(proxyRequest: ProxyRequest, apiKeyOverride: String? = null): Any?

    suspend fun delete(proxyRequest: ProxyRequest, apiKeyOverride: String? = null): Any?
}

enum class HttpMethod {
    GET,
    POST,
    PATCH,
    PUT,
    DELETE
}

const val BT_EXPOSE_RAW_PROXY_RESPONSE_HEADER = "bt-expose-raw-proxy-response"

class ProxyRequest {
    var path: String? = null
    var queryParams: Map<String, String>? = emptyMap()
    var headers: Map<String, String>? = emptyMap()
    var body: Any? = null
}

class ProxyApi(
    val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    val apiClientProvider: (apiKeyOverride: String?) -> ApiClient
) : Proxy {

    override suspend fun get(proxyRequest: ProxyRequest, apiKeyOverride: String?): Any? =
        withContext(dispatcher) {
            proxy(HttpMethod.GET.name, proxyRequest, apiKeyOverride)
        }

    override suspend fun post(proxyRequest: ProxyRequest, apiKeyOverride: String?): Any? =
        withContext(dispatcher) {
            proxy(HttpMethod.POST.name, proxyRequest, apiKeyOverride)
        }

    override suspend fun put(proxyRequest: ProxyRequest, apiKeyOverride: String?): Any? =
        withContext(dispatcher) {
            proxy(HttpMethod.PUT.name, proxyRequest, apiKeyOverride)
        }

    override suspend fun patch(proxyRequest: ProxyRequest, apiKeyOverride: String?): Any? =
        withContext(dispatcher) {
            proxy(HttpMethod.PATCH.name, proxyRequest, apiKeyOverride)
        }

    override suspend fun delete(proxyRequest: ProxyRequest, apiKeyOverride: String?): Any? =
        withContext(dispatcher) {
            proxy(HttpMethod.DELETE.name, proxyRequest, apiKeyOverride)
        }

    private fun proxy(method: String, proxyRequest: ProxyRequest, apiKeyOverride: String?): Any? {
        val apiClient = apiClientProvider(apiKeyOverride)
        var body = proxyRequest.body

        if (body != null) {
            body = if (body::class.java.isPrimitiveType()) body
            else if (body is TextElement) body.getTransformedText()
            else if (body is ElementValueReference) body.getValue()
            else replaceElementRefs(body.toMap())
        }

        val call = apiClient.buildCall(
            "${apiClient.basePath}/proxy",
            proxyRequest.path ?: "",
            method,
            proxyRequest.queryParams?.toPairs(),
            emptyList(),
            body,
            proxyRequest.headers,
            emptyMap(),
            emptyMap(),
            arrayOf("ApiKey"),
            null
        )
        val returnType: Type = object : com.google.gson.reflect.TypeToken<Any?>() {}.type
        val response = apiClient.execute<Any>(call, returnType)


        if (response.headers.containsKey(BT_EXPOSE_RAW_PROXY_RESPONSE_HEADER)) {
            return response.data
        }

        return transformResponseToValueReferences(response.data)
    }

    private fun Map<String, String>.toPairs(): List<com.basistheory.Pair> =
        this.map {
            com.basistheory.Pair(it.key, it.value)
        }
}

fun Any?.tryGetElementValueReference(path: String): ElementValueReference? {
    if (this == null || path.isEmpty()) return null

    val pathSegments = path.split(".")
    val matchResult = Regex("^(\\w*)(?:\\[(\\d+)])?$").matchEntire(pathSegments.first())
    val pathSegment = matchResult?.groups?.elementAtOrNull(1)?.value
    val indexSegment = matchResult?.groups?.elementAtOrNull(2)?.value?.toIntOrNull()

    val value = pathSegment?.let {
        val map = this as? Map<*, *> ?: return null
        map[pathSegment]?.let {
            if (indexSegment != null) {
                val collection = it as? Collection<*> ?: return null
                collection.elementAt(indexSegment)
            } else it
        }
    }

    return if (pathSegments.count() > 1)
        value?.tryGetElementValueReference(pathSegments.drop(1).joinToString("."))
    else
        value as ElementValueReference?
}

fun Any?.getElementValueReference(path: String): ElementValueReference =
    this.tryGetElementValueReference(path) ?: throw NoSuchElementException()
