import com.basistheory.android.service.HttpMethod
import com.basistheory.android.util.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException

interface Client {
    suspend fun get(url: String, headers: Map<String, String>? = emptyMap()): Any?
    suspend fun post(url: String, body: Any?, headers: Map<String, String>? = emptyMap()): Any?
    suspend fun put(url: String, body: Any?, headers: Map<String, String>? = emptyMap()): Any?
    suspend fun patch(url: String, body: Any?, headers: Map<String, String>? = emptyMap()): Any?
    suspend fun delete(url: String, headers: Map<String, String>? = emptyMap()): Any?
}

class HttpClient(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : Client {
    private val client: OkHttpClient = OkHttpClient()

    override suspend fun post(url: String, body: Any?, headers: Map<String, String>?): Any =
        withContext(dispatcher) {
            send(url, HttpMethod.POST, body, headers)
        }

    override suspend fun get(url: String, headers: Map<String, String>?): Any? =
        withContext(dispatcher) {
            send(url, HttpMethod.GET, null, headers)
        }

    override suspend fun put(url: String, body: Any?, headers: Map<String, String>?): Any? =
        withContext(dispatcher) {
            send(url, HttpMethod.PUT, body, headers)
        }

    override suspend fun patch(url: String, body: Any?, headers: Map<String, String>?): Any? =
        withContext(dispatcher) {
            send(url, HttpMethod.PATCH, body, headers)
        }

    override suspend fun delete(url: String, headers: Map<String, String>?): Any? =
        withContext(dispatcher) {
            send(url, HttpMethod.DELETE, null, headers)
        }

    private fun requestBuilder(
        url: String,
        method: HttpMethod,
        body: Any?,
        headers: Map<String, String>
    ): Request {
        val contentType = headers["Content-Type"] ?: "application/json"

        val requestBody = body?.let { getElementsValues(it) }?.let {
            mapObjToRequestBody(contentType, it)
        }

        val req = Request.Builder()
            .url(url)
            .method(
                method.toString(),
                requestBody
            )

        for ((key, value) in headers.entries) {
            req.addHeader(key, value)
        }

        return req.build()
    }

    private suspend fun send(
        url: String,
        method: HttpMethod,
        body: Any?,
        headers: Map<String, String>?
    ): String = withContext(dispatcher) {
        val req = requestBuilder(url, method, body, headers ?: emptyMap())
        val call = client.newCall(req)
        val response = call.execute()

        if (response.isSuccessful) {
            response.body?.string() ?: ""
        } else {
            throw IOException("$method request failed with response code: ${response.code}")
        }
    }
}
