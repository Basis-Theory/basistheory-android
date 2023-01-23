package com.basistheory.android.service

import com.basistheory.ApiClient
import com.basistheory.android.util.transformResponseToValueReferences
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

class ProxyRequest {
    var path: String? = null
    var queryParams: Map<String, String>? = emptyMap()
    var headers: Map<String, String>? = emptyMap()
    var body: Any? = null
}

class ProxyApi(val apiClientProvider: (apiKeyOverride: String?) -> ApiClient) : Proxy {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun get(proxyRequest: ProxyRequest, apiKeyOverride: String?): Any? =
        withContext(ioDispatcher) {
            proxy(HttpMethod.GET.name, proxyRequest, apiKeyOverride)
        }

    override suspend fun post(proxyRequest: ProxyRequest, apiKeyOverride: String?): Any? =
        withContext(ioDispatcher) {
            proxy(HttpMethod.POST.name, proxyRequest, apiKeyOverride)
        }

    override suspend fun put(proxyRequest: ProxyRequest, apiKeyOverride: String?): Any? =
        withContext(ioDispatcher) {
            proxy(HttpMethod.PUT.name, proxyRequest, apiKeyOverride)
        }

    override suspend fun patch(proxyRequest: ProxyRequest, apiKeyOverride: String?): Any? =
        withContext(ioDispatcher) {
            proxy(HttpMethod.PATCH.name, proxyRequest, apiKeyOverride)
        }

    override suspend fun delete(proxyRequest: ProxyRequest, apiKeyOverride: String?): Any? =
        withContext(ioDispatcher) {
            proxy(HttpMethod.DELETE.name, proxyRequest, apiKeyOverride)
        }

    private fun proxy(method: String, proxyRequest: ProxyRequest, apiKeyOverride: String?): Any? {
        val apiClient = apiClientProvider(apiKeyOverride)

        val call = apiClient.buildCall(
            "https://api.basistheory.com/proxy",
            proxyRequest.path ?: "",
            method,
            proxyRequest.queryParams?.toPairs(),
            emptyList(),
            proxyRequest.body,
            proxyRequest.headers,
            emptyMap(),
            emptyMap(),
            arrayOf("ApiKey"),
            null
        )
        val returnType: Type = object : com.google.gson.reflect.TypeToken<Any?>() {}.type
        val response = apiClient.execute<Any>(call, returnType).data

        return transformResponseToValueReferences(response)
    }

    private fun Map<String, String>.toPairs(): List<com.basistheory.Pair> =
        this.map {
            com.basistheory.Pair(it.key, it.value)
        }
}