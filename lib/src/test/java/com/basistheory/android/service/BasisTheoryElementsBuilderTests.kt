package com.basistheory.android.service

import kotlinx.coroutines.Dispatchers
import org.junit.Test
import strikt.api.expectCatching
import strikt.assertions.isA
import strikt.assertions.isSuccess

class BasisTheoryElementsBuilderTests {

    @Test
    fun `can build default instance without using any setters`() {
        expectCatching {
            BasisTheoryElements.builder()
                .build()
        }.isSuccess().isA<BasisTheoryElements>()
    }

    @Test
    fun `can build instance with custom parameters`() {
        expectCatching {
            BasisTheoryElements.builder()
                .apiKey("api_key")
                .apiUrl("https://my-custom-api.basistheory.com")
                .ioDispatcher(Dispatchers.Unconfined)
                .build()
        }.isSuccess().isA<BasisTheoryElements>()
    }
}
