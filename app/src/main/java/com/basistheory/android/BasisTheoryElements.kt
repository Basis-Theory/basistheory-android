package com.basistheory.android

import com.basistheory.Configuration
import com.basistheory.TokenizeApi
import com.basistheory.auth.ApiKeyAuth
import io.github.cdimascio.dotenv.dotenv

class BasisTheoryElements {
    companion object {
        private val dotenv = dotenv {
            directory = "/assets"
            filename = "env"
        }

        fun tokenize(body: Any): Any {
            val client = Configuration.getDefaultApiClient()
            client.basePath = dotenv["BASIS_THEORY_API_URL"] ?: "https://api.basistheory.com"
            (client.getAuthentication("ApiKey") as ApiKeyAuth).let {
                it.apiKey = dotenv["BASIS_THEORY_API_KEY"]
            }

            val tokenizeApi = TokenizeApi(client)
            return tokenizeApi.tokenize(body)
        }
    }
}