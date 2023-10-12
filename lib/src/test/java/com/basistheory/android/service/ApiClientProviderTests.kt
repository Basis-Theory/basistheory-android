package com.basistheory.android.service

import com.basistheory.ApiClient
import com.basistheory.android.BuildConfig
import com.basistheory.auth.ApiKeyAuth
import kotlinx.coroutines.Dispatchers
import net.datafaker.Faker
import org.junit.Test
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.*
import java.util.UUID

class ApiClientProviderTests {

    @Test
    fun `getTokenizeApi throws when initialized without an api key`() {
        val provider = ApiClientProvider()

        expectCatching { provider.getTokenizeApi() }
            .isFailure()
            .isA<IllegalArgumentException>()
    }

    @Test
    fun `getTokenizeApi configures client with default api key and url`() =
        configuresApiClientWithDefaults { provider ->
            provider.getTokenizeApi().apiClient
        }

    @Test
    fun `getTokensApi configures client with default api key and url`() =
        configuresApiClientWithDefaults { provider ->
            provider.getTokensApi().apiClient
        }

    @Test
    fun `getSessionsApi configures client with default api key and url`() =
        configuresApiClientWithDefaults { provider ->
            provider.getSessionsApi().apiClient
        }

    @Test
    fun `getProxyApi configures client with default api key, url and dispatcher`() =
        configuresApiClientWithDefaults { provider ->
                val proxyApi = provider.getProxyApi()
                expectThat(proxyApi.dispatcher).isEqualTo(Dispatchers.IO)
                proxyApi.apiClientProvider(null)
        }

    @Test
    fun `getProxyApi configures client with overridden dispatcher`() =
        configuresApiClientWithDefaults { provider ->
            val proxyApi = provider.getProxyApi(Dispatchers.Main)
            expectThat(proxyApi.dispatcher).isEqualTo(Dispatchers.Main)
            proxyApi.apiClientProvider(null)
        }

    @Test
    fun `getApiClient configures client with overridden api key`() {
        val apiUrl = Faker().internet().url()
        val defaultApiKey = UUID.randomUUID().toString()
        val apiKeyOverride = UUID.randomUUID().toString()

        val provider = ApiClientProvider(apiUrl, defaultApiKey)

        val client = provider.getTokenizeApi(apiKeyOverride)

        expectThat(client.apiClient) {
            get { apiUrl }.isEqualTo(apiUrl)
            get { getAuthentication("ApiKey") }
                .isA<ApiKeyAuth>().and {
                    get { apiKey }.isEqualTo(apiKeyOverride)
                }
        }
    }

    @Test
    fun `getApiClient configures client with user-agent header`() {
        val apiUrl = Faker().internet().url()
        val defaultApiKey = UUID.randomUUID().toString()
        val apiKeyOverride = UUID.randomUUID().toString()

        val provider = ApiClientProvider(apiUrl, defaultApiKey)

        val client = provider.getTokenizeApi(apiKeyOverride)

        // the java openapi generator doesn't publicly expose default headers
        // using reflection as a workaround
        val defaultHeadersField = client.apiClient::class.java
            .getDeclaredField("defaultHeaderMap")
            .apply { isAccessible = true }

        @Suppress("UNCHECKED_CAST")
        val defaultHeaders: Map<String, String>? =
            defaultHeadersField.get(client.apiClient) as? Map<String, String>

        val defaultUserAgent = defaultHeaders?.get("User-Agent")
        expectThat(defaultUserAgent).isNotNull()
            .startsWith("basistheory-android/${BuildConfig.VERSION_NAME}")
    }

    private fun configuresApiClientWithDefaults(
        createApiClient: (provider: ApiClientProvider) -> ApiClient
    ) {
        val apiUrl = Faker().internet().url()
        val defaultApiKey = UUID.randomUUID().toString()
        val provider = ApiClientProvider(apiUrl, defaultApiKey)

        val apiClient = createApiClient(provider)

        expectThat(apiClient) {
            get { apiUrl }.isEqualTo(apiUrl)
            get { getAuthentication("ApiKey") }
                .isA<ApiKeyAuth>().and {
                    get { apiKey }.isEqualTo(defaultApiKey)
                }
        }
    }
}