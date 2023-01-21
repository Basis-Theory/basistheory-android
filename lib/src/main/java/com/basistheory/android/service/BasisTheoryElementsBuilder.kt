package com.basistheory.android.service

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class BasisTheoryElementsBuilder {
    private var _apiKey: String? = null
    private var _apiUrl: String = "https://api.basistheory.com"
    private var _dispatcher: CoroutineDispatcher = Dispatchers.IO

    fun apiKey(value: String): BasisTheoryElementsBuilder {
        _apiKey = value
        return this
    }

    fun apiUrl(value: String): BasisTheoryElementsBuilder {
        _apiUrl = value
        return this
    }

    fun dispatcher(value: CoroutineDispatcher): BasisTheoryElementsBuilder {
        _dispatcher = value
        return this
    }

    fun build(): BasisTheoryElements =
        BasisTheoryElements(
            ApiClientProvider(_apiUrl, _apiKey),
            _dispatcher
        )
}