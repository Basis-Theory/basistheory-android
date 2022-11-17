package com.basistheory.android.service

import kotlinx.coroutines.Dispatchers
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isNotEqualTo

/**
 * Note: strikt has an open issue where isNotNull does not resolve properly
 * https://github.com/robfletcher/strikt/issues/260
 */
class BasisTheoryElementsBuilderTests {

    @Test
    fun `can build default instance`() {
        val bt = BasisTheoryElements.builder()
            .build()

        expectThat(bt).isNotEqualTo(null)
    }

    @Test
    fun `can build instance with custom parameters`() {
        val bt = BasisTheoryElements.builder()
            .apiKey("api_key")
            .apiUrl("https://my-custom-api.basistheory.com")
            .ioDispatcher(Dispatchers.Unconfined)
            .build()

        expectThat(bt).isNotEqualTo(null)
    }

}