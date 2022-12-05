package com.basistheory.android.example

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.basistheory.android.service.BasisTheoryElements
import com.basistheory.android.view.CardNumberElement
import com.basistheory.android.view.CardVerificationCodeElement
import com.basistheory.android.view.KeyboardType
import com.basistheory.android.view.TextElement
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import org.threeten.bp.Instant
import org.threeten.bp.temporal.ChronoUnit

class MainActivity : AppCompatActivity() {
    private lateinit var cardNumberElement: CardNumberElement
    private lateinit var cvcElement: CardVerificationCodeElement
    private lateinit var nameElement: TextElement
    private lateinit var phoneNumberElement: TextElement
    private lateinit var socialSecurityNumberElement: TextElement
    private lateinit var orderNumberElement: TextElement
    private lateinit var tokenizeResult: TextView
    private lateinit var tokenizeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cardNumberElement = findViewById(R.id.cardNumber)
        cvcElement = findViewById(R.id.cvc)
        nameElement = findViewById(R.id.name)
        phoneNumberElement = findViewById(R.id.phoneNumber)
        socialSecurityNumberElement = findViewById(R.id.socialSecurityNumber)
        orderNumberElement = findViewById(R.id.orderNumber)
        tokenizeResult = findViewById(R.id.tokenizeResult)
        tokenizeButton = findViewById(R.id.tokenizeButton)

        val digitRegex = Regex("""\d""")
        val charRegex = Regex("""[A-Za-z]""")

        phoneNumberElement.keyboardType = KeyboardType.NUMBER
        phoneNumberElement.mask = listOf(
            "+",
            "1",
            "(",
            digitRegex,
            digitRegex,
            digitRegex,
            ")",
            " ",
            digitRegex,
            digitRegex,
            digitRegex,
            "-",
            digitRegex,
            digitRegex,
            digitRegex,
            digitRegex
        )

        orderNumberElement.mask =
            listOf(charRegex, charRegex, charRegex, "-", digitRegex, digitRegex, digitRegex)

        // example of how an app could implement validation
        cardNumberElement.addChangeEventListener {
            if (!it.isValid && it.isComplete) {
                cardNumberElement.textColor = Color.RED
                tokenizeButton.isEnabled = false
            } else {
                cardNumberElement.textColor = Color.BLACK
                tokenizeButton.isEnabled = true
            }
        }
    }

    fun setText(button: View) {
        assert(button.id == R.id.setTextButton)

        cardNumberElement.setText("4242424242424242")
        cvcElement.setText("123")
        nameElement.setText("Manually Set Name")
        phoneNumberElement.setText("2345678900")
        socialSecurityNumberElement.setText("234567890")
        orderNumberElement.setText("ABC123")
    }

    fun submit(button: View) {
        assert(button.id == R.id.tokenizeButton)

        val bt = BasisTheoryElements.builder()
            .apiUrl(BuildConfig.BASIS_THEORY_API_URL)
            .apiKey(BuildConfig.BASIS_THEORY_API_KEY)
            .build()

        /**
         * Note: java.time.Instant is only supported on API level 26+.
         * threetenbp is a backport of java.time for java 6/7 and Android API < 26
         */
        val expirationTimestamp = Instant.now().plus(5, ChronoUnit.MINUTES).toString()

        runBlocking {
            val tokenizeResponse = bt.tokenize(object {
                val type = "token"
                val data = object {
                    val staticProp = "Static Value"
                    val cardNumber = cardNumberElement
                    val cvc = cvcElement
                    val name = nameElement
                    val phoneNumber = phoneNumberElement
                    val socialSecurityNumber = socialSecurityNumberElement
                    val orderNumber = orderNumberElement
                }
                val expires_at = expirationTimestamp
            })

            val gson = GsonBuilder().setPrettyPrinting().create()

            tokenizeResult.text = gson.toJson(tokenizeResponse)
        }
    }
}
