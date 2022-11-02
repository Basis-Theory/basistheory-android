package com.basistheory.android.service

import com.basistheory.auth.ApiKeyAuth
import com.github.javafaker.Faker
import org.junit.Test
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isFailure
import java.util.UUID

class ApiClientProviderTests {

    @Test
    fun `tokenizeApi throws when initialized without an api key`() {
        val provider = ApiClientProvider()

        expectCatching { provider.getTokenizeApi() }
            .isFailure()
            .isA<IllegalArgumentException>()
    }

    @Test
    fun `tokenizeApi configures client with default api key and url`() {
        val apiUrl = Faker().internet().url()
        val defaultApiKey = UUID.randomUUID().toString()
        val provider = ApiClientProvider(apiUrl, defaultApiKey)

        val client = provider.getTokenizeApi()

        expectThat(client.apiClient) {
            get { apiUrl }.isEqualTo(apiUrl)
            get { getAuthentication("ApiKey") }
                .isA<ApiKeyAuth>().and {
                    get { apiKey }.isEqualTo(defaultApiKey)
                }
        }
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
}