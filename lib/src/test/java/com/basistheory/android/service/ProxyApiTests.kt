package com.basistheory.android.service

import com.basistheory.ApiClient
import com.basistheory.ApiResponse
import com.basistheory.android.model.ElementValueReference
import io.mockk.every
import io.mockk.impl.annotations.SpyK
import io.mockk.junit4.MockKRule
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okio.Buffer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import strikt.api.expectThat
import strikt.assertions.*
import java.util.*

@RunWith(JUnitParamsRunner::class)
class ProxyApiTests {

    @get:Rule
    val mockkRule = MockKRule(this)

    @SpyK
    private var apiClient: ApiClient = spyk()

    private val proxyApi: ProxyApi = ProxyApi(Dispatchers.IO) { apiClient }

    private var proxyRequest: ProxyRequest = ProxyRequest()

    @Before
    fun setUp() {
        every {
            apiClient.buildCall(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } answers { callOriginal() }
    }

    @Test
    @Parameters(method = "proxyMethodsTestsInput")
    fun `should execute proxy request with the provided values`(
        httpMethod: HttpMethod,
        contentType: String?,
        contentsSubType: String?,
        requestBody: String?
    ) {
        val queryParamValue = UUID.randomUUID().toString()
        proxyRequest = proxyRequest.apply {
            path = "/payment"
            headers = mapOf(
                "BT-PROXY-URL" to "https://echo.basistheory.com/post",
                "Content-Type" to "text/plain"
            )
            queryParams = mapOf("param" to queryParamValue)
            body = requestBody
        }

        val callSlot = slot<Call>()
        every { apiClient.execute<Any>(capture(callSlot), any()) } returns ApiResponse(
            200,
            emptyMap(),
            "Hello World"
        )

        val result = runBlocking {
            when (httpMethod) {
                HttpMethod.GET -> proxyApi.get(proxyRequest)
                HttpMethod.POST -> proxyApi.post(proxyRequest)
                HttpMethod.PATCH -> proxyApi.patch(proxyRequest)
                HttpMethod.PUT -> proxyApi.put(proxyRequest)
                HttpMethod.DELETE -> proxyApi.delete(proxyRequest)
            }
        }

        verify(exactly = 1) { apiClient.execute<Any>(any(), any()) }

        expectThat(callSlot.captured.request()) {
            get { method }.isEqualTo(httpMethod.name)
            get { url.toString() }
                .isEqualTo("https://api.basistheory.com/proxy/payment?param=${queryParamValue}")
            get { headers["BT-PROXY-URL"] }.isEqualTo("https://echo.basistheory.com/post")
            get { body?.contentType()?.type }.isEqualTo(contentType)
            get { body?.contentType()?.subtype }.isEqualTo(contentsSubType)

            if (this.subject.body != null) {
                val buffer = Buffer()
                this.subject.body!!.writeTo(buffer)
                val bodyInRequest = buffer.readUtf8()
                expectThat(bodyInRequest).isEqualTo(requestBody)
            } else {
                get { body }.isNull()
            }
        }

        expectThat(result).isA<ElementValueReference>()
    }

    @Test
    fun `should transform complex proxy response to element value references`() {
        val callSlot = slot<Call>()
        every { apiClient.execute<Any>(capture(callSlot), any()) } returns ApiResponse(
            200,
            emptyMap(),
            linkedMapOf(
                "customer_id" to "102023201931949",
                "id" to null,
                "card" to linkedMapOf(
                    "number" to "4242424242424242",
                    "expiration_month" to "10",
                    "expiration_year" to "2026",
                    "cvc" to "123",
                ),
                "pii" to linkedMapOf(
                    "name" to linkedMapOf(
                        "first_name" to "Drewsue",
                        "last_name" to "Webuino"
                    )
                )
            )
        )

        val result = runBlocking {
            proxyApi.post(proxyRequest)
        }

        expectThat(result.tryGetElementValueReference("id")).isNull()
        expectThat(result.tryGetElementValueReference("invalid_path")).isNull()
        expectThat((result.tryGetElementValueReference("customer_id"))).isNotNull()

        expectThat(result.tryGetElementValueReference("card.number")).isNotNull()
        expectThat(result.tryGetElementValueReference("card.expiration_month")).isNotNull()
        expectThat(result.tryGetElementValueReference("card.expiration_year")).isNotNull()
        expectThat(result.tryGetElementValueReference("card.cvc")).isNotNull()

        expectThat(result.tryGetElementValueReference("pii.name.first_name")).isNotNull()
        expectThat(result.tryGetElementValueReference("pii.name.last_name")).isNotNull()
    }

    @Test
    fun `should transform array proxy response to element value references`() {
        val callSlot = slot<Call>()
        every { apiClient.execute<Any>(capture(callSlot), any()) } returns ApiResponse(
            200,
            emptyMap(),
            arrayOf(
                "foo",
                null,
                "bar",
                null,
                "yaz",
                "qux"
            )
        )

        val result = runBlocking {
            proxyApi.post(proxyRequest)
        }

        expectThat(
            (result as List<Any?>).filterNotNull().all { it is ElementValueReference }).isTrue()
    }

    private fun proxyMethodsTestsInput(): Array<Any?> {
        return arrayOf(
            arrayOf(HttpMethod.GET, null, null, null),
            arrayOf(HttpMethod.DELETE, null, null, null),
            arrayOf(HttpMethod.POST, "text", "plain", "Hello World"),
            arrayOf(HttpMethod.PATCH, "text", "plain", "Hello World"),
            arrayOf(HttpMethod.PUT, "text", "plain", "Hello World")
        )
    }

}