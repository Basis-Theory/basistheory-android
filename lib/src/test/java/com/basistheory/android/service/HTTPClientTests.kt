import junitparams.JUnitParamsRunner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(JUnitParamsRunner::class)
class HttpClientTests {
    private lateinit var server: MockWebServer
    private lateinit var client: HttpClient


    private val endpoint = "/api"

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        client = HttpClient(Dispatchers.IO)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun testGetRequest() = runBlocking {
        server.enqueue(MockResponse().setBody("Got it"))

        val url = server.url(endpoint).toString()

        client.get(url)

        val request = server.takeRequest()
        assertEquals("GET", request.method)
        assertEquals(endpoint, request.path)
    }

    @Test
    fun testPostRequest() = runBlocking {
        val expectedRequestBody = "Request body"
        server.enqueue(MockResponse().setBody("Posted it"))

        val url = server.url(endpoint).toString()

        client.post(url, expectedRequestBody)

        val request = server.takeRequest()
        assertEquals("POST", request.method)
        assertEquals(endpoint, request.path)
        assertEquals(expectedRequestBody, request.body.readUtf8())
    }

    @Test
    fun testPutRequest() = runBlocking {
        val expectedRequestBody = "Request body"
        server.enqueue(MockResponse().setBody("Put it"))

        val url = server.url(endpoint).toString()

        client.put(url, expectedRequestBody)

        val request = server.takeRequest()
        assertEquals("PUT", request.method)
        assertEquals(endpoint, request.path)
        assertEquals(expectedRequestBody, request.body.readUtf8())
    }

    @Test
    fun testPatchRequest() = runBlocking {
        val expectedRequestBody = "Request body"
        server.enqueue(MockResponse().setBody("Patched it"))

        val url = server.url(endpoint).toString()
        client.patch(url, expectedRequestBody)

        val request = server.takeRequest()
        assertEquals("PATCH", request.method)
        assertEquals(endpoint, request.path)
        assertEquals(expectedRequestBody, request.body.readUtf8())
    }

    @Test
    fun testDeleteRequest() = runBlocking {
        server.enqueue(MockResponse().setBody("Deleted it"))

        val url = server.url(endpoint).toString()

        client.delete(url)

        val request = server.takeRequest()

        assertEquals("DELETE", request.method)
        assertEquals(endpoint, request.path)
    }


    @Test
    fun testFormUrlEncodedPostRequest() = runBlocking {
        val expectedRequestBody = object {
            val type = "card"
            val billing_details = object {
                val name = "Peter Panda"
            }
            val card = object {
                val number = "1234567890"
                val exp_month = 12
                val exp_year = 2023
                val cvc = 123
            }
        }

        server.enqueue(MockResponse().setBody("Encoded it"))

        val url = server.url(endpoint).toString()

        client.post(
            url,
            expectedRequestBody,
            headers = mapOf("Content-Type" to "application/x-www-form-urlencoded")
        )

        val request = server.takeRequest()
        assertEquals("POST", request.method)
        assertEquals(endpoint, request.path)
        assertEquals(
            "type=card&billing_details%5Bname%5D=Peter+Panda&card%5Bnumber%5D=1234567890&card%5Bexp_month%5D=12&card%5Bexp_year%5D=2023&card%5Bcvc%5D=123",
            request.body.readUtf8()
        )
    }

    @Test
    fun testDefaultsContentTypeToApplicationJsonWhenNoContentTypeIsProvided() = runBlocking {
        val reqBody = object {
            val foo = "bar"
        }

        server.enqueue(MockResponse().setBody("Defaulted it"))

        val url = server.url(endpoint).toString()

        client.post(
            url,
            reqBody,
        )

        val request = server.takeRequest()

        assert(request.headers["Content-Type"]!!.contains("application/json"))
    }


    @Test
    fun `testHandlesUnsuccessfulResponsesFromTheRemote`() = runBlocking {
        val reqBody = object {
            val foo = "bar"
        }

        server.enqueue(MockResponse().setBody("Everything is broken").setResponseCode(500))

        val url = server.url(endpoint).toString()

        val exception = assertThrows(IOException::class.java) {
           runBlocking {
               client.post(url, reqBody) as IOException
           }
        }

        assertEquals("POST request failed with response code: 500", exception.message)
    }

    @Test
    fun `testThrowsWhenContentTypeIsNotSupported`() = runBlocking {
        val reqBody = object {
            val foo = "bar"
        }

        server.enqueue(MockResponse().setBody("Everything is broken").setResponseCode(200))

        val url = server.url(endpoint).toString()

        val exception = assertThrows(IOException::class.java) {
            runBlocking {
                client.post(url, reqBody, mapOf("Content-Type" to "text/plain"))
            }
        }

        assertEquals("Content-Type not supported", exception.message)
    }
}
