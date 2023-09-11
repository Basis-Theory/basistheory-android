package com.basistheory.android.service

import HttpClient
import com.basistheory.CreateSessionResponse
import com.basistheory.CreateTokenRequest
import com.basistheory.CreateTokenResponse
import com.basistheory.Token
import com.basistheory.android.constants.ElementValueType
import com.basistheory.android.model.ElementValueReference
import com.basistheory.android.util.*
import com.basistheory.android.util.getElementsValues
import com.basistheory.android.util.replaceElementRefs
import com.basistheory.android.util.tryGetTextToTokenize
import com.basistheory.android.view.TextElement
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BasisTheoryElements internal constructor(
    private val apiClientProvider: ApiClientProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    val proxy: ProxyApi = apiClientProvider.getProxyApi(dispatcher)
    val client = HttpClient(dispatcher)

    @JvmOverloads
    suspend fun tokenize(body: Any, apiKeyOverride: String? = null): Any =
        withContext(dispatcher) {
            val tokenizeApiClient = apiClientProvider.getTokenizeApi(apiKeyOverride)
            val request = getElementsValues(body)

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
                else if (createTokenRequest.data is TextElement) (createTokenRequest.data as TextElement).tryGetTextToTokenize().toValueType((createTokenRequest.data as TextElement).getValueType)
                else if (createTokenRequest.data is ElementValueReference) (createTokenRequest.data as ElementValueReference).getValue().toValueType((createTokenRequest.data as ElementValueReference).getValueType)
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

    @JvmOverloads
    suspend fun getToken(
        id: String,
        apiKeyOverride: String? = null
    ): Token =
        withContext(dispatcher) {
            val tokensApi = apiClientProvider.getTokensApi(apiKeyOverride)

            tokensApi.getById(id).also {
                it.data = transformResponseToValueReferences(it.data)
            }
        }

    companion object {
        @JvmStatic
        fun builder(): BasisTheoryElementsBuilder = BasisTheoryElementsBuilder()
    }
}

fun String?.toValueType(getValueType: ElementValueType?): Any? {
    return when(getValueType) {
        ElementValueType.INTEGER -> {
            this?.toInt()
        }

        ElementValueType.DOUBLE -> {
            this?.toDouble()
        }

        ElementValueType.BOOLEAN -> {
            this?.toBoolean()
        }

        else -> {
            this;
        }
    }
}

fun <T> Any?.getValue(path: String): T = this.tryGetValue(path) ?: throw NoSuchElementException()

fun <T> Any?.tryGetValue(path: String): T? {
    if (this == null || path.isEmpty()) return null

    val pathSegments = path.split(".")
    val map = this as? Map<*, *> ?: return null

    val value = map[pathSegments.first()]

    return if (pathSegments.count() > 1)
        value?.tryGetValue(pathSegments.drop(1).joinToString("."))
    else
        value as? T?
}
