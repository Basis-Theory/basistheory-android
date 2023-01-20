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
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okio.Buffer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isNull
import strikt.assertions.isTrue
import java.util.*

@RunWith(JUnitParamsRunner::class)
class ProxyApiTests {

    @get:Rule
    val mockkRule = MockKRule(this)

    @SpyK
    private var apiClient: ApiClient = spyk()

    private val proxyApi: ProxyApi = ProxyApi { apiClient }

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
        every { apiClient.execute<Any>(capture(callSlot)) } returns ApiResponse(
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

        verify(exactly = 1) { apiClient.execute<Any>(any()) }

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
        every { apiClient.execute<Any>(capture(callSlot)) } returns ApiResponse(
            200,
            emptyMap(),
            object {
                val customer_id = "102023201931949"
                val id = null
                val card = object {
                    val number = "4242424242424242"
                    val expiration_month = "10"
                    val expiration_year = "2026"
                    val cvc = "123"
                }
                val pii = object {
                    val name = object {
                        val first_name = "Drewsue"
                        val last_name = "Webuino"
                    }
                }
            }
        )

        val result = runBlocking {
            proxyApi.post(proxyRequest)
        }

        expectThat(((result as Map<*, *>)["id"])).isNull()
        expectThat((result["customer_id"])).isA<ElementValueReference>()

        expectThat((result["card"] as Map<*, *>)["number"]).isA<ElementValueReference>()
        expectThat((result["card"] as Map<*, *>)["expiration_month"]).isA<ElementValueReference>()
        expectThat((result["card"] as Map<*, *>)["expiration_year"]).isA<ElementValueReference>()
        expectThat((result["card"] as Map<*, *>)["cvc"]).isA<ElementValueReference>()

        expectThat(((result["pii"] as Map<*, *>)["name"] as Map<*, *>)["first_name"]).isA<ElementValueReference>()
        expectThat(((result["pii"] as Map<*, *>)["name"] as Map<*, *>)["last_name"]).isA<ElementValueReference>()
    }

    @Test
    fun `should transform array proxy response to element value references`() {
        val callSlot = slot<Call>()
        every { apiClient.execute<Any>(capture(callSlot)) } returns ApiResponse(
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

        expectThat((result as List<Any?>).filterNotNull().all { it is ElementValueReference }).isTrue()
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