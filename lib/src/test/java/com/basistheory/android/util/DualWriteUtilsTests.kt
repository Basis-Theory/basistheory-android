import com.basistheory.android.util.mapObjToRequestBody
import com.basistheory.android.util.convertObjectToFormUrlEncoded
import com.basistheory.android.util.encodeParamsToFormUrlEncoded
import com.github.javafaker.Faker
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class DualWriteUtilsTests {
    @Test
    fun testMapBodyToContentType_Json() {
        val contentType = "application/json"
        val obj = object {
            val name = "John"
            val age = 30
        }
        val expectedRequestBody = obj.toString().toRequestBody(contentType.toMediaType())

        val requestBody = mapObjToRequestBody(contentType, obj)

        assertTrue(requestBody is RequestBody)
        // RequestBody appends the charset by default
        assertEquals(expectedRequestBody.contentType(), "$contentType; charset=utf-8".toMediaType())
        assertEquals(expectedRequestBody.contentLength(), requestBody?.contentLength())
    }

    @Test
    fun testMapBodyToContentType_FormUrlEncoded() {
        val contentType = "application/x-www-form-urlencoded"
        val obj = object {
            val name = "John"
            val age = 30
        }
        val expectedRequestBody = encodeParamsToFormUrlEncoded(convertObjectToFormUrlEncoded(obj))
            .toRequestBody(contentType.toMediaType())

        val requestBody = mapObjToRequestBody(contentType, obj)

        assertTrue(requestBody is RequestBody)
        assertEquals(expectedRequestBody.contentType(), "$contentType; charset=utf-8".toMediaType())
        assertEquals(expectedRequestBody.contentLength(), requestBody?.contentLength())
    }

    @Test(expected = IOException::class)
    fun testMapBodyToContentType_UnsupportedContentType() {
        val contentType = "text/plain"
        val obj = "Sample text"

        mapObjToRequestBody(contentType, obj)
    }

    @Test
    fun testConvertObjectToFormUrlEncoded() {
        val obj = mapOf(
            "name" to "John",
            "age" to 30,
            "address" to mapOf("city" to "New York", "country" to "USA"),
            "hobbies" to listOf("reading", "painting")
        )

        val expectedFormParams = mapOf(
            "name" to "John",
            "age" to "30",
            "address[city]" to "New York",
            "address[country]" to "USA",
            "hobbies[0]" to "reading",
            "hobbies[1]" to "painting"
        )

        val formParams = convertObjectToFormUrlEncoded(obj)

        assertEquals(expectedFormParams, formParams)
    }

    @Test
    fun testEncodeParamsToFormUrlEncoded() {
        val formParams = mapOf(
            "name" to "John",
            "age" to "30",
            "address[city]" to "New York",
            "address[country]" to "USA",
            "hobbies[0]" to "reading",
            "hobbies[1]" to "painting"
        )

        val expectedEncodedString =
            "name=John&age=30&address%5Bcity%5D=New+York&address%5Bcountry%5D=USA&hobbies%5B0%5D=reading&hobbies%5B1%5D=painting"

        val encodedString = encodeParamsToFormUrlEncoded(formParams)

        assertEquals(expectedEncodedString, encodedString)
    }
}
