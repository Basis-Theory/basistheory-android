package com.basistheory.android

import com.basistheory.Configuration
import com.basistheory.TokenizeApi
import com.basistheory.auth.ApiKeyAuth

class BasisTheoryElements {
    companion object {
        fun tokenize(body: Any): Any {
            val client = Configuration.getDefaultApiClient()
            client.basePath = "https://api-dev.basistheory.com"
            (client.getAuthentication("ApiKey") as ApiKeyAuth).let {
                it.apiKey = ""
            }

            val tokenizeApi = TokenizeApi(client)
            return tokenizeApi.tokenize(body)
        }
    }
}