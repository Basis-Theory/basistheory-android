package com.basistheory.android.service

import com.basistheory.CreateSessionResponse
import com.basistheory.CreateTokenRequest
import com.basistheory.CreateTokenResponse
import com.basistheory.android.model.ElementValueReference
import com.basistheory.android.model.exceptions.IncompleteElementException
import com.basistheory.android.util.isPrimitiveType
import com.basistheory.android.util.toMap
import com.basistheory.android.view.TextElement
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BasisTheoryElements internal constructor(
    private val apiClientProvider: ApiClientProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    val proxy: ProxyApi = apiClientProvider.getProxyApi(dispatcher)

    @JvmOverloads
    suspend fun tokenize(body: Any, apiKeyOverride: String? = null): Any =
        withContext(dispatcher) {
            val tokenizeApiClient = apiClientProvider.getTokenizeApi(apiKeyOverride)
            val request =
                if (body::class.java.isPrimitiveType()) body
                else if (body is TextElement) body.getTransformedText()
                else if (body is ElementValueReference) body.getValue()
                else replaceElementRefs(body.toMap())

            tokenizeApiClient.tokenize(request)
        }

    @JvmOverloads
    suspend fun createToken(
        createTokenRequest: CreateTokenRequest,
        apiKeyOverride: String? = null
    ): CreateTokenResponse =
        withContext(dispatcher) {
            val tokensApi = apiClientProvider.getTokensApi(apiKeyOverride)
            val data =
                if (createTokenRequest.data == null) null
                else if (createTokenRequest.data!!::class.java.isPrimitiveType()) createTokenRequest.data
                else if (createTokenRequest.data is TextElement) (createTokenRequest.data as TextElement).tryGetTextToTokenize()
                else if (createTokenRequest.data is ElementValueReference) (createTokenRequest.data as ElementValueReference).getValue()
                else replaceElementRefs(createTokenRequest.data!!.toMap())

            createTokenRequest.data = data

            tokensApi.create(createTokenRequest)
        }

    @JvmOverloads
    suspend fun createSession(apiKeyOverride: String? = null): CreateSessionResponse =
        withContext(dispatcher) {
            val sessionsApi = apiClientProvider.getSessionsApi(apiKeyOverride)
            sessionsApi.create()
        }

    private fun replaceElementRefs(map: MutableMap<String, Any?>): MutableMap<String, Any?> {
        for ((key, value) in map) {
            if (value == null) continue
            val fieldType = value::class.java
            if (!fieldType.isPrimitiveType()) {
                when (value) {
                    is TextElement -> {
                        map[key] = value.tryGetTextToTokenize()
                    }
                    is ElementValueReference -> {
                        map[key] = value.getValue()
                    }
                    else -> {
                        val children = value.toMap()
                        map[key] = children
                        replaceElementRefs(children)
                    }
                }
            }
        }

        return map
    }

    private fun TextElement.tryGetTextToTokenize(): String? {
        if (!this.isComplete)
            throw IncompleteElementException(this.id)

        return this.getTransformedText()
    }

    companion object {
        @JvmStatic
        fun builder(): BasisTheoryElementsBuilder = BasisTheoryElementsBuilder()
    }
}
