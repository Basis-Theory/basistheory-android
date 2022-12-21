package com.basistheory.android.service

import android.app.Activity
import com.basistheory.CreateTokenRequest
import com.basistheory.TokenizeApi
import com.basistheory.TokensApi
import com.basistheory.android.view.CardExpirationDateElement
import com.basistheory.android.view.CardNumberElement
import com.basistheory.android.view.CardVerificationCodeElement
import com.basistheory.android.view.TextElement
import com.github.javafaker.Faker
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*
import javax.inject.Inject

@RunWith(RobolectricTestRunner::class)
class BasisTheoryElementsTests {
    private val faker = Faker()
    private lateinit var nameElement: TextElement
    private lateinit var phoneNumberElement: TextElement
    private lateinit var cardNumberElement: CardNumberElement
    private lateinit var cardExpElement: CardExpirationDateElement
    private lateinit var cvcElement: CardVerificationCodeElement

    @get:Rule
    val mockkRule = MockKRule(this)

    @RelaxedMockK
    private lateinit var tokenizeApi: TokenizeApi

    @RelaxedMockK
    private lateinit var tokensApi: TokensApi

    @RelaxedMockK
    private lateinit var provider: ApiClientProvider

    @Inject
    private val dispatcher = Dispatchers.Unconfined

    @InjectMockKs
    private lateinit var bt: BasisTheoryElements

    @Before
    fun setUp() {
        val activity = Robolectric.buildActivity(Activity::class.java).get()

        nameElement = TextElement(activity)
        phoneNumberElement = TextElement(activity)
        cardNumberElement = CardNumberElement(activity)
        cardExpElement = CardExpirationDateElement(activity)
        cvcElement = CardVerificationCodeElement(activity)
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
                }
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
                    )
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

            val cardNumber = faker.business().creditCardNumber()
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
            verify { tokenizeApi.tokenize(month) }

            bt.tokenize(cardExpElement.year())
            verify { tokenizeApi.tokenize(year) }
        }

    @Test
    fun `tokenize should replace Element refs within request object with underlying data values`() =
        runBlocking {
            every { provider.getTokenizeApi(any()) } returns tokenizeApi

            val name = faker.name().fullName()
            nameElement.setText(name)

            val phoneNumber = faker.phoneNumber().phoneNumber()
            phoneNumberElement.setText(phoneNumber)

            val cardNumber = faker.business().creditCardNumber()
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
                        "expMonth" to expMonth,
                        "expYear" to expYear,
                        "cvc" to cvc
                    ),
                    "nested" to mapOf(
                        "raw" to request.data.nested.raw,
                        "phoneNumber" to phoneNumber
                    )
                )
            )

            verify { tokenizeApi.tokenize(expectedRequest) }
        }

    @Test
    fun `createToken should pass api key override to ApiClientProvider`() = runBlocking {
        val apiKeyOverride = UUID.randomUUID().toString()

        every { provider.getTokensApi(any()) } returns tokensApi

        bt.createToken(CreateTokenRequest(), apiKeyOverride)

        verify { provider.getTokensApi(apiKeyOverride) }
    }

    @Test
    fun `createToken should forward top level primitive value without modification`() =
        runBlocking {
            every { provider.getTokensApi(any()) } returns tokensApi

            val name = faker.name().fullName()
            val createTokenRequest = createTokenRequest(name)
            bt.createToken(createTokenRequest)

            verify { tokensApi.create(createTokenRequest) }
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
                    )
                )
            val expectedRequest = createTokenRequest(expectedData)

            verify { tokensApi.create(expectedRequest) }
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

            verify { tokensApi.create(expectedRequest) }
        }

    @Test
    fun `createToken should replace top level CardElement ref with underlying data value`() =
        runBlocking {
            every { provider.getTokensApi(any()) } returns tokensApi

            val cardNumber = faker.business().creditCardNumber()
            cardNumberElement.setText(cardNumber)

            val createTokenRequest = createTokenRequest(cardNumberElement)

            bt.createToken(createTokenRequest)

            val expectedRequest = createTokenRequest(cardNumber.replace(Regex("""[^\d]"""), ""))

            verify { tokensApi.create(expectedRequest) }
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

            val expectedMonthRequest = createTokenRequest(month)
            verify { tokensApi.create(expectedMonthRequest) }

            bt.createToken(createTokenRequestYear)

            val expectedYearRequest = createTokenRequest(year)
            verify { tokensApi.create(expectedYearRequest) }
        }

    @Test
    fun `createToken should replace Element refs within request object with underlying data values`() =
        runBlocking {
            every { provider.getTokensApi(any()) } returns tokensApi

            val name = faker.name().fullName()
            nameElement.setText(name)

            val phoneNumber = faker.phoneNumber().phoneNumber()
            phoneNumberElement.setText(phoneNumber)

            val cardNumber = faker.business().creditCardNumber()
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
                        "expMonth" to expMonth,
                        "expYear" to expYear,
                        "cvc" to cvc
                    ),
                    "nested" to mapOf(
                        "raw" to data.data.nested.raw,
                        "phoneNumber" to phoneNumber
                    )
                )
            )

            val expectedCreateTokenRequest = createTokenRequest(expectedData)

            verify { tokensApi.create(expectedCreateTokenRequest) }
        }

    private fun createTokenRequest(data: Any): CreateTokenRequest =
        CreateTokenRequest().apply {
            this.data = data
        }
}