package com.basistheory.android.service

import android.app.Activity
import android.view.View
import com.basistheory.ApiClient
import com.basistheory.ApiResponse
import com.basistheory.SessionsApi
import com.basistheory.Token
import com.basistheory.TokenizeApi
import com.basistheory.TokensApi
import com.basistheory.android.constants.ElementValueType
import com.basistheory.android.model.CreateTokenRequest
import com.basistheory.android.model.ElementValueReference
import com.basistheory.android.model.exceptions.IncompleteElementException
import com.basistheory.android.model.toJava
import com.basistheory.android.view.CardExpirationDateElement
import com.basistheory.android.view.CardNumberElement
import com.basistheory.android.view.CardVerificationCodeElement
import com.basistheory.android.view.TextElement
import com.github.javafaker.Faker
import io.mockk.Called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit4.MockKRule
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okio.Buffer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isFailure
import strikt.assertions.isNotEqualTo
import strikt.assertions.isNull
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import javax.inject.Inject

@Config(sdk = [33]) // TODO remove once Roboelectric releases a new version supporting SDK 34 https://github.com/robolectric/robolectric/issues/8404
@RunWith(RobolectricTestRunner::class)
class BasisTheoryElementsTests {
    private val faker = Faker()
    private lateinit var nameElement: TextElement
    private lateinit var phoneNumberElement: TextElement
    private lateinit var cardNumberElement: CardNumberElement
    private lateinit var cardExpElement: CardExpirationDateElement
    private lateinit var cvcElement: CardVerificationCodeElement

    private lateinit var textElement: TextElement
    private lateinit var intElement: TextElement
    private lateinit var doubleElement: TextElement
    private lateinit var boolElement: TextElement

    // faker's test card numbers are not all considered complete by our elements; use these in tests below
    private val testCardNumbers = listOf(
        "4242424242424242",
        "4000056655665556",
        "5555555555554444",
        "2223003122003222",
        "5200828282828210",
        "5105105105105100",
        "378282246310005",
        "371449635398431",
        "6011111111111117",
        "6011000990139424",
        "3056930009020004",
        "36227206271667",
        "3566002020360505",
        "6200000000000005"
    )

    @get:Rule
    val mockkRule = MockKRule(this)

    @RelaxedMockK
    private lateinit var tokenizeApi: TokenizeApi

    @RelaxedMockK
    private lateinit var tokensApi: TokensApi

    @RelaxedMockK
    private lateinit var sessionsApi: SessionsApi

    @RelaxedMockK
    private lateinit var proxyApi: ProxyApi

    @RelaxedMockK
    private lateinit var provider: ApiClientProvider

    @Inject
    private val dispatcher = Dispatchers.Unconfined

    @InjectMockKs
    private lateinit var bt: BasisTheoryElements

    @SpyK
    private var apiClient: ApiClient = spyk()

    private val testProxyApi: ProxyApi = ProxyApi(Dispatchers.IO) { apiClient }

    private var proxyRequest: ProxyRequest = ProxyRequest()

    @Before
    fun setUp() {
        val activity = Robolectric.buildActivity(Activity::class.java).get()

        nameElement = TextElement(activity).also { it.id = View.generateViewId() }
        phoneNumberElement = TextElement(activity).also { it.id = View.generateViewId() }
        cardNumberElement = CardNumberElement(activity).also { it.id = View.generateViewId() }
        cardExpElement = CardExpirationDateElement(activity).also { it.id = View.generateViewId() }
        cvcElement = CardVerificationCodeElement(activity).also { it.id = View.generateViewId() }

        textElement = TextElement(activity).also { it.id = View.generateViewId() }
        intElement = TextElement(activity).also { it.id = View.generateViewId() }
        doubleElement = TextElement(activity).also { it.id = View.generateViewId() }
        boolElement = TextElement(activity).also { it.id = View.generateViewId() }
    }

    @Test
    fun `tokenize should pass api key override to ApiClientProvider`() = runBlocking {
        val apiKeyOverride = UUID.randomUUID().toString()

        every { provider.getTokenizeApi(any()) } returns tokenizeApi

        bt.tokenize(object {}, apiKeyOverride)

        verify { provider.getTokenizeApi(apiKeyOverride) }
    }

    @Test
    fun `tokenize should forward top level primitive value without modification`() =
        runBlocking {
            every { provider.getTokenizeApi(any()) } returns tokenizeApi

            val name = faker.name().fullName()
            bt.tokenize(name)

            verify { tokenizeApi.tokenize(name) }
        }

    @Test
    fun `tokenize should forward primitive data values within request without modification`() =
        runBlocking {
            every { provider.getTokenizeApi(any()) } returns tokenizeApi

            val request = object {
                val type = "token"
                val data = "primitive"
            }

            bt.tokenize(request)

            val expectedRequest = mapOf<String, Any?>(
                "type" to request.type,
                "data" to request.data
            )

            verify { tokenizeApi.tokenize(expectedRequest) }
        }

    @Test
    fun `tokenize should forward complex data values within request without modification`() =
        runBlocking {
            every { provider.getTokenizeApi(any()) } returns tokenizeApi

            val request = object {
                val type = "token"
                val data = object {
                    val string = faker.lorem().word()
                    val int = faker.random().nextInt(10, 100)
                    val nullValue = null
                    val nested = object {
                        val double = faker.random().nextDouble()
                        val bool = faker.random().nextBoolean()
                        val timestamp = Instant.now().toString()
                        val nullValue = null
                    }
                    val array = arrayOf(
                        faker.lorem().word(),
                        faker.random().nextDouble(),
                        faker.random().nextBoolean(),
                        null
                    )
                }
                val containers = arrayOf(
                    "/test/1/",
                    "/test/2/"
                )
            }

            bt.tokenize(request)

            val expectedRequest = mapOf<String, Any?>(
                "type" to request.type,
                "data" to mapOf(
                    "string" to request.data.string,
                    "int" to request.data.int,
                    "nullValue" to null,
                    "nested" to mapOf(
                        "double" to request.data.nested.double,
                        "bool" to request.data.nested.bool,
                        "timestamp" to request.data.nested.timestamp,
                        "nullValue" to null,
                    ),
                    "array" to arrayListOf(
                        request.data.array[0],
                        request.data.array[1],
                        request.data.array[2],
                        null
                    )
                ),
                "containers" to arrayListOf(
                    request.containers[0],
                    request.containers[1]
                )
            )

            verify { tokenizeApi.tokenize(expectedRequest) }
        }

    @Test
    fun `tokenize should replace top level TextElement ref with underlying data value`() =
        runBlocking {
            every { provider.getTokenizeApi(any()) } returns tokenizeApi

            val name = faker.name().fullName()
            nameElement.setText(name)

            bt.tokenize(nameElement)

            verify { tokenizeApi.tokenize(name) }
        }

    @Test
    fun `tokenize should replace top level CardElement ref with underlying data value`() =
        runBlocking {
            every { provider.getTokenizeApi(any()) } returns tokenizeApi

            val cardNumber = testCardNumbers.random()
            cardNumberElement.setText(cardNumber)

            bt.tokenize(cardNumberElement)

            val expectedTokenizedCardNumber = cardNumber.replace(Regex("""[^\d]"""), "")
            verify { tokenizeApi.tokenize(expectedTokenizedCardNumber) }
        }

    @Test
    fun `tokenize should replace top level CardExpirationDateElement refs with underlying data value`() =
        runBlocking {
            every { provider.getTokenizeApi(any()) } returns tokenizeApi

            val expDate = LocalDate.now().plus(2, ChronoUnit.YEARS)
            val month = expDate.monthValue.toString().padStart(2, '0')
            val year = expDate.year.toString()
            cardExpElement.setText("$month/${year.takeLast(2)}")

            bt.tokenize(cardExpElement.month())
            verify { tokenizeApi.tokenize(expDate.monthValue) }

            bt.tokenize(cardExpElement.year())
            verify { tokenizeApi.tokenize(expDate.year) }

            bt.tokenize(cardExpElement.format("MM"))
            verify { tokenizeApi.tokenize(month) }

            bt.tokenize(cardExpElement.format("yyyy"))
            verify { tokenizeApi.tokenize(year) }

            if (month.take(1) == "0") {
                bt.tokenize(cardExpElement.format("M"))
                verify { tokenizeApi.tokenize(month.takeLast(1)) }
            } else {
                bt.tokenize(cardExpElement.format("M"))
                verify { tokenizeApi.tokenize(month) }
            }

            bt.tokenize(cardExpElement.format("yyyyMM"))
            verify { tokenizeApi.tokenize(year + month) }

            bt.tokenize(cardExpElement.format("MM/yyyy"))
            verify { tokenizeApi.tokenize("$month/$year") }

            bt.tokenize(cardExpElement.format("MM/yy"))
            verify { tokenizeApi.tokenize("$month/${year.takeLast(2)}") }

            bt.tokenize(cardExpElement.format("MM-yyyy"))
            verify { tokenizeApi.tokenize("$month-$year") }
        }

    @Test
    fun `tokenize should replace Element refs within request object with underlying data values`() =
        runBlocking {
            every { provider.getTokenizeApi(any()) } returns tokenizeApi

            val name = faker.name().fullName()
            nameElement.setText(name)

            val phoneNumber = faker.phoneNumber().phoneNumber()
            phoneNumberElement.setText(phoneNumber)

            val cardNumber = testCardNumbers.random()
            cardNumberElement.setText(cardNumber)

            val expDate = LocalDate.now().plus(2, ChronoUnit.YEARS)
            val expMonth = expDate.monthValue.toString().padStart(2, '0')
            val expYear = expDate.year.toString()
            cardExpElement.setText("$expMonth/${expYear.takeLast(2)}")

            val cvc = faker.random().nextInt(100, 999).toString()
            cvcElement.setText(cvc)

            val request = object {
                val type = "token"
                val data = object {
                    val raw = faker.lorem().word()
                    val name = nameElement
                    val card = object {
                        val number = cardNumberElement
                        val expMonth = cardExpElement.month()
                        val expYear = cardExpElement.year()
                        val cvc = cvcElement
                    }
                    val nested = object {
                        val raw = faker.lorem().word()
                        val phoneNumber = phoneNumberElement
                    }
                    val array = arrayOf(
                        nameElement,
                        phoneNumberElement,
                        null
                    )
                    val arrayList = arrayListOf(
                        nameElement,
                        phoneNumberElement,
                        null
                    )
                }
            }

            bt.tokenize(request)

            val expectedRequest = mapOf<String, Any?>(
                "type" to request.type,
                "data" to mapOf(
                    "raw" to request.data.raw,
                    "name" to name,
                    "card" to mapOf(
                        "number" to cardNumber.replace(Regex("""[^\d]"""), ""),
                        "expMonth" to expDate.monthValue,
                        "expYear" to expDate.year,
                        "cvc" to cvc
                    ),
                    "nested" to mapOf(
                        "raw" to request.data.nested.raw,
                        "phoneNumber" to phoneNumber
                    ),
                    "array" to arrayListOf(
                        name,
                        phoneNumber,
                        null
                    ),
                    "arrayList" to arrayListOf(
                        name,
                        phoneNumber,
                        null
                    )
                )
            )

            verify { tokenizeApi.tokenize(expectedRequest) }
        }

    @Test
    fun `tokenize should respect getValueType type when sending values to the API`() = runBlocking {
        every { provider.getTokenizeApi(any()) } returns tokenizeApi

        val testString = faker.name().firstName()
        val testInt = faker.number().numberBetween(1, 10)
        val testDouble = faker.number().randomDouble(2, 10, 99)
        val testBoolean = faker.bool().bool()

        // individual
        textElement.setText(testString)
        bt.tokenize(textElement)
        verify { tokenizeApi.tokenize(testString) }

        intElement.setText(testInt.toString())
        intElement.getValueType = ElementValueType.INTEGER
        bt.tokenize(intElement)
        verify { tokenizeApi.tokenize(testInt) }

        doubleElement.setText(testDouble.toString())
        doubleElement.getValueType = ElementValueType.DOUBLE
        bt.tokenize(doubleElement)
        verify { tokenizeApi.tokenize(testDouble) }

        boolElement.setText(testBoolean.toString())
        boolElement.getValueType = ElementValueType.BOOLEAN
        bt.tokenize(boolElement)
        verify { tokenizeApi.tokenize(testBoolean) }

        // grouped
        val request = object {
            val type = "token"
            val data = object {
                val text = textElement
                val int = intElement
                val double = doubleElement
                val bool = boolElement
            }
        }

        bt.tokenize(request)

        val expectedRequest = mapOf<String, Any?>(
            "type" to request.type,
            "data" to mapOf(
                "text" to testString,
                "int" to testInt,
                "double" to testDouble,
                "bool" to testBoolean
            )
        )
        verify { tokenizeApi.tokenize(expectedRequest) }
    }

    @Test
    fun `createToken should pass api key override to ApiClientProvider`() = runBlocking {
        val apiKeyOverride = UUID.randomUUID().toString()

        every { provider.getTokensApi(any()) } returns tokensApi

        bt.createToken(CreateTokenRequest(type = "token", data = ""), apiKeyOverride)

        verify { provider.getTokensApi(apiKeyOverride) }
    }

    @Test
    fun `createToken should forward top level primitive value without modification`() =
        runBlocking {
            every { provider.getTokensApi(any()) } returns tokensApi

            val name = faker.name().fullName()
            val createTokenRequest = createTokenRequest(name)
            bt.createToken(createTokenRequest)

            verify { tokensApi.create(createTokenRequest.toJava()) }
        }

    @Test
    fun `createToken should forward complex data values within request without modification`() =
        runBlocking {
            every { provider.getTokensApi(any()) } returns tokensApi

            val data = object {
                val string = faker.lorem().word()
                val int = faker.random().nextInt(10, 100)
                val nullValue = null
                val nested = object {
                    val double = faker.random().nextDouble()
                    val bool = faker.random().nextBoolean()
                    val timestamp = Instant.now().toString()
                    val nullValue = null
                }
                val array = arrayOf(
                    faker.lorem().word(),
                    faker.random().nextDouble(),
                    faker.random().nextBoolean(),
                    null
                )
            }
            val request = createTokenRequest(data)

            bt.createToken(request)

            val expectedData = mapOf(
                "string" to data.string,
                "int" to data.int,
                "nullValue" to null,
                "nested" to mapOf(
                    "double" to data.nested.double,
                    "bool" to data.nested.bool,
                    "timestamp" to data.nested.timestamp,
                    "nullValue" to null
                ),
                "array" to arrayListOf(
                    data.array[0],
                    data.array[1],
                    data.array[2],
                    null
                )
            )
            val expectedRequest = createTokenRequest(expectedData)

            verify { tokensApi.create(expectedRequest.toJava()) }
        }

    @Test
    fun `createToken should replace top level TextElement ref with underlying data value`() =
        runBlocking {
            every { provider.getTokensApi(any()) } returns tokensApi

            val name = faker.name().fullName()
            nameElement.setText(name)

            val createTokenRequest = createTokenRequest(nameElement)

            bt.createToken(createTokenRequest)

            val expectedRequest = createTokenRequest(name)

            verify { tokensApi.create(expectedRequest.toJava()) }
        }

    @Test
    fun `createToken should replace top level CardElement ref with underlying data value`() =
        runBlocking {
            every { provider.getTokensApi(any()) } returns tokensApi

            val cardNumber = testCardNumbers.random()
            cardNumberElement.setText(cardNumber)

            val createTokenRequest = createTokenRequest(cardNumberElement)

            bt.createToken(createTokenRequest)

            val expectedRequest = createTokenRequest(cardNumber.replace(Regex("""[^\d]"""), ""))

            verify { tokensApi.create(expectedRequest.toJava()) }
        }

    @Test
    fun `createToken should replace top level CardExpirationDateElement refs with underlying data value`() =
        runBlocking {
            every { provider.getTokensApi(any()) } returns tokensApi

            val expDate = LocalDate.now().plus(2, ChronoUnit.YEARS)
            val month = expDate.monthValue.toString().padStart(2, '0')
            val year = expDate.year.toString()
            cardExpElement.setText("$month/${year.takeLast(2)}")

            val createTokenRequestMonth = createTokenRequest(cardExpElement.month())
            val createTokenRequestYear = createTokenRequest(cardExpElement.year())

            bt.createToken(createTokenRequestMonth)

            val expectedMonthRequest = createTokenRequest(expDate.monthValue)
            verify { tokensApi.create(expectedMonthRequest.toJava()) }

            bt.createToken(createTokenRequestYear)

            val expectedYearRequest = createTokenRequest(expDate.year)
            verify { tokensApi.create(expectedYearRequest.toJava()) }
        }

    @Test
    fun `createToken should replace Element refs within request object with underlying data values`() =
        runBlocking {
            every { provider.getTokensApi(any()) } returns tokensApi

            val name = faker.name().fullName()
            nameElement.setText(name)

            val phoneNumber = faker.phoneNumber().phoneNumber()
            phoneNumberElement.setText(phoneNumber)

            val cardNumber = testCardNumbers.random()
            cardNumberElement.setText(cardNumber)

            val expDate = LocalDate.now().plus(2, ChronoUnit.YEARS)
            val expMonth = expDate.monthValue.toString().padStart(2, '0')
            val expYear = expDate.year.toString()
            cardExpElement.setText("$expMonth/${expYear.takeLast(2)}")

            val cvc = faker.random().nextInt(100, 999).toString()
            cvcElement.setText(cvc)

            val data = object {
                val type = "token"
                val data = object {
                    val raw = faker.lorem().word()
                    val name = nameElement
                    val card = object {
                        val number = cardNumberElement
                        val expMonth = cardExpElement.month()
                        val expYear = cardExpElement.year()
                        val cvc = cvcElement
                    }
                    val nested = object {
                        val raw = faker.lorem().word()
                        val phoneNumber = phoneNumberElement
                    }
                    val array = arrayOf(
                        nameElement,
                        phoneNumberElement,
                        null
                    )
                }
            }
            val createTokenRequest = createTokenRequest(data)

            bt.createToken(createTokenRequest)

            val expectedData = mapOf<String, Any?>(
                "type" to data.type,
                "data" to mapOf(
                    "raw" to data.data.raw,
                    "name" to name,
                    "card" to mapOf(
                        "number" to cardNumber.replace(Regex("""[^\d]"""), ""),
                        "expMonth" to expDate.monthValue,
                        "expYear" to expDate.year,
                        "cvc" to cvc
                    ),
                    "nested" to mapOf(
                        "raw" to data.data.nested.raw,
                        "phoneNumber" to phoneNumber
                    ),
                    "array" to arrayListOf(
                        name,
                        phoneNumber,
                        null
                    )
                )
            )

            val expectedCreateTokenRequest = createTokenRequest(expectedData)

            verify { tokensApi.create(expectedCreateTokenRequest.toJava()) }
        }

    @Test
    fun `createToken should respect getValueType type when sending values to the API`() = runBlocking {
        every { provider.getTokensApi(any()) } returns tokensApi

        val testString = faker.name().firstName()
        val testInt = faker.number().numberBetween(1, 10)
        val testDouble = faker.number().randomDouble(2, 10, 99)
        val testBoolean = faker.bool().bool()

        textElement.setText(testString)

        intElement.setText(testInt.toString())
        intElement.getValueType = ElementValueType.INTEGER

        doubleElement.setText(testDouble.toString())
        doubleElement.getValueType = ElementValueType.DOUBLE

        boolElement.setText(testBoolean.toString())
        boolElement.getValueType = ElementValueType.BOOLEAN

        val request = object {
            val type = "token"
            val data = object {
                val text = textElement
                val int = intElement
                val double = doubleElement
                val bool = boolElement
            }
        }

        val createTokenRequest = createTokenRequest(request)

        bt.createToken(createTokenRequest)

        val expectedRequest = mapOf<String, Any?>(
            "type" to request.type,
            "data" to mapOf(
                "text" to testString,
                "int" to testInt,
                "double" to testDouble,
                "bool" to testBoolean
            )
        )

        val expectedCreateTokenRequest = createTokenRequest(expectedRequest)

        verify { tokensApi.create(expectedCreateTokenRequest.toJava()) }
    }

    @Test
    fun `proxy should replace Element refs within request object with underlying data values`() {
        val name = faker.name().fullName()
        nameElement.setText(name)

        val phoneNumber = faker.phoneNumber().phoneNumber()
        phoneNumberElement.setText(phoneNumber)


        var data = object {
            val name = nameElement
            val phone = phoneNumberElement
        }

        val stringifiedData = "{\"name\":\"${name}\",\"phone\":\"${phoneNumber}\"}"

        proxyRequest = proxyRequest.apply {
            headers = mapOf(
                "BT-PROXY-URL" to "https://echo.basistheory.com/post",
                "Content-Type" to "application/json"
            )
            body = data
        }

        val callSlot = slot<Call>()
        every { apiClient.execute<Any>(capture(callSlot), any()) } returns ApiResponse(
            200,
            emptyMap(),
            "Hello World"
        )

        val result = runBlocking {
            testProxyApi.post(proxyRequest)
        }

        verify(exactly = 1) { apiClient.execute<Any>(any(), any()) }

        expectThat(callSlot.captured.request()) {
            get { headers["BT-PROXY-URL"] }.isEqualTo("https://echo.basistheory.com/post")
            get { body?.contentType()?.type }.isEqualTo("application")
            get { body?.contentType()?.subtype }.isEqualTo("json")

            if (this.subject.body != null) {
                val buffer = Buffer()
                this.subject.body!!.writeTo(buffer)
                val bodyInRequest = buffer.readUtf8()
                expectThat(bodyInRequest).isEqualTo(stringifiedData)
            } else {
                get { body }.isNull()
            }
        }

        expectThat(result).isA<ElementValueReference>()
    }

    @Test
    fun `proxy should replace top level TextElement ref with underlying data value`() {
        val name = faker.name().fullName()
        nameElement.setText(name)

        proxyRequest = proxyRequest.apply {
            headers = mapOf(
                "BT-PROXY-URL" to "https://echo.basistheory.com/post",
                "Content-Type" to "text/plain"
            )
            body = nameElement
        }

        val callSlot = slot<Call>()
        every { apiClient.execute<Any>(capture(callSlot), any()) } returns ApiResponse(
            200,
            emptyMap(),
            "Hello World"
        )

        val result = runBlocking {
            testProxyApi.post(proxyRequest)
        }

        verify(exactly = 1) { apiClient.execute<Any>(any(), any()) }

        expectThat(callSlot.captured.request()) {
            get { headers["BT-PROXY-URL"] }.isEqualTo("https://echo.basistheory.com/post")
            get { body?.contentType()?.type }.isEqualTo("text")
            get { body?.contentType()?.subtype }.isEqualTo("plain")

            if (this.subject.body != null) {
                val buffer = Buffer()
                this.subject.body!!.writeTo(buffer)
                val bodyInRequest = buffer.readUtf8()
                expectThat(bodyInRequest).isEqualTo(name)
            } else {
                get { body }.isNull()
            }
        }

        expectThat(result).isA<ElementValueReference>()
    }

    @Test
    fun `proxy should replace top level CardNumberElement ref with underlying data value`() {
        val cardNumber = testCardNumbers.random()
        cardNumberElement.setText(cardNumber)

        proxyRequest = proxyRequest.apply {
            headers = mapOf(
                "BT-PROXY-URL" to "https://echo.basistheory.com/post",
                "Content-Type" to "text/plain"
            )
            body = cardNumberElement
        }

        val callSlot = slot<Call>()
        every { apiClient.execute<Any>(capture(callSlot), any()) } returns ApiResponse(
            200,
            emptyMap(),
            "Hello World"
        )

        val result = runBlocking {
            testProxyApi.post(proxyRequest)
        }

        verify(exactly = 1) { apiClient.execute<Any>(any(), any()) }

        expectThat(callSlot.captured.request()) {
            get { headers["BT-PROXY-URL"] }.isEqualTo("https://echo.basistheory.com/post")
            get { body?.contentType()?.type }.isEqualTo("text")
            get { body?.contentType()?.subtype }.isEqualTo("plain")

            if (this.subject.body != null) {
                val buffer = Buffer()
                this.subject.body!!.writeTo(buffer)
                val bodyInRequest = buffer.readUtf8()
                expectThat(bodyInRequest).isEqualTo(cardNumber.replace(Regex("""[^\d]"""), ""))
            } else {
                get { body }.isNull()
            }
        }

        expectThat(result).isA<ElementValueReference>()
    }

    @Test
    fun `proxy should replace top level CardExpirationDateElement ref with underlying data value`() {
        val expDate = LocalDate.now().plus(2, ChronoUnit.YEARS)
        val month = expDate.monthValue.toString().padStart(2, '0')
        val year = expDate.year.toString()
        val expDateString = "$month/${year.takeLast(2)}"
        cardExpElement.setText(expDateString)

        proxyRequest = proxyRequest.apply {
            headers = mapOf(
                "BT-PROXY-URL" to "https://echo.basistheory.com/post",
                "Content-Type" to "text/plain"
            )
            body = cardExpElement
        }

        val callSlot = slot<Call>()
        every { apiClient.execute<Any>(capture(callSlot), any()) } returns ApiResponse(
            200,
            emptyMap(),
            "Hello World"
        )

        val result = runBlocking {
            testProxyApi.post(proxyRequest)
        }

        verify(exactly = 1) { apiClient.execute<Any>(any(), any()) }

        expectThat(callSlot.captured.request()) {
            get { headers["BT-PROXY-URL"] }.isEqualTo("https://echo.basistheory.com/post")
            get { body?.contentType()?.type }.isEqualTo("text")
            get { body?.contentType()?.subtype }.isEqualTo("plain")

            if (this.subject.body != null) {
                val buffer = Buffer()
                this.subject.body!!.writeTo(buffer)
                val bodyInRequest = buffer.readUtf8()
                expectThat(bodyInRequest).isEqualTo(expDateString)
            } else {
                get { body }.isNull()
            }
        }

        expectThat(result).isA<ElementValueReference>()
    }

    // note: junit only supports one @RunWith class per test class, so we can't use JUnitParamsRunner here
    @Test
    fun `throws IncompleteElementException when attempting to tokenize luhn-invalid card`() =
        incompleteCardThrowsIncompleteElementException("4242424242424245")

    @Test
    fun `throws IncompleteElementException when attempting to tokenize partial card`() =
        incompleteCardThrowsIncompleteElementException("424242")

    @Test
    fun `throws IncompleteElementException when attempting to tokenize invalid expiration dates`() =
        runBlocking {
            every { provider.getTokensApi(any()) } returns tokensApi

            cardExpElement.setText("11/01")

            val createTokenRequest = createTokenRequest(object {
                val expirationMonth = cardExpElement.month()
                val expirationYear = cardExpElement.year()
            })

            expectCatching { bt.createToken(createTokenRequest) }
                .isFailure()
                .isA<IncompleteElementException>().and {
                    get { message }.isEqualTo(
                        IncompleteElementException.errorMessageFor(
                            cardExpElement.id
                        )
                    )
                }

            verify { tokensApi.create(any()) wasNot Called }
        }

    @Test
    fun `createSession should call java SDK without api key override`() = runBlocking {
        every { provider.getSessionsApi(any()) } returns sessionsApi

        bt.createSession()

        verify { provider.getSessionsApi() }
        verify { sessionsApi.create() }
    }

    @Test
    fun `createSession should call java SDK with api key override`() = runBlocking {
        val apiKeyOverride = UUID.randomUUID().toString()

        every { provider.getSessionsApi(any()) } returns sessionsApi

        bt.createSession(apiKeyOverride)

        verify { provider.getSessionsApi(apiKeyOverride) }
        verify { sessionsApi.create() }
    }

    @Test
    fun `getToken should call java SDK without api key override`() = runBlocking {
        val tokenId = UUID.randomUUID().toString()

        every { provider.getTokensApi(any()) } returns tokensApi
        every { tokensApi.getById(tokenId) } returns fakeToken()

        bt.getToken(tokenId)

        verify { provider.getTokensApi() }
        verify { tokensApi.getById(tokenId) }
    }

    @Test
    fun `getToken should call java SDK with api key override`() = runBlocking {
        val tokenId = UUID.randomUUID().toString()
        val apiKeyOverride = UUID.randomUUID().toString()

        every { provider.getTokensApi(any()) } returns tokensApi
        every { tokensApi.getById(tokenId) } returns fakeToken()

        bt.getToken(tokenId, apiKeyOverride)

        verify { provider.getTokensApi(apiKeyOverride) }
        verify { tokensApi.getById(tokenId) }
    }

    @Test
    fun `provides a proxy instance`() = runBlocking {
        every { provider.getProxyApi(any()) } returns proxyApi

        expectThat(bt.proxy).isNotEqualTo(null)

        verify { provider.getProxyApi(any()) }
    }

    private fun createTokenRequest(data: Any): CreateTokenRequest =
        CreateTokenRequest(type = "token", data = data)

    private fun incompleteCardThrowsIncompleteElementException(
        incompleteCardNumber: String
    ) = runBlocking {
        every { provider.getTokensApi(any()) } returns tokensApi

        cardNumberElement.setText(incompleteCardNumber)

        val createTokenRequest = createTokenRequest(cardNumberElement)

        expectCatching { bt.createToken(createTokenRequest) }
            .isFailure()
            .isA<IncompleteElementException>().and {
                get { message }.isEqualTo(
                    IncompleteElementException.errorMessageFor(
                        cardNumberElement.id
                    )
                )
            }

        verify { tokensApi.create(any()) wasNot Called }
    }

    private fun fakeToken(): Token =
        Token().apply {
            id = UUID.randomUUID().toString()
            tenantId = UUID.randomUUID()
            type = "token"
            data = Faker.instance().name().firstName()
            createdBy = UUID.randomUUID()
            createdAt = OffsetDateTime.now()
            containers = mutableListOf("/general")
        }
}