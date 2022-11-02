package com.basistheory.android.service

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class BasisTheoryElementsBuilder {
    private var _apiKey: String? = null
    private var _apiUrl: String = "https://api.basistheory.com"
    private var _ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    fun apiKey(value: String): BasisTheoryElementsBuilder {
        _apiKey = value
        return this
    }

    fun apiUrl(value: String): BasisTheoryElementsBuilder {
        _apiUrl = value
        return this
    }

    fun ioDispatcher(value: CoroutineDispatcher): BasisTheoryElementsBuilder {
        _ioDispatcher = value
        return this
    }

    fun build(): BasisTheoryElements =
        BasisTheoryElements(
            ApiClientProvider(_apiUrl, _apiKey),
            _ioDispatcher
        )
}