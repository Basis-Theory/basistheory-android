package com.basistheory.example

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.basistheory.android.BasisTheoryElements
import com.basistheory.android.TextElement
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import org.threeten.bp.Instant
import org.threeten.bp.temporal.ChronoUnit

class MainActivity : AppCompatActivity() {
    private lateinit var nameElement: TextElement
    private lateinit var phoneNumberElement: TextElement
    private lateinit var tokenizeResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nameElement = findViewById(R.id.name)
        phoneNumberElement = findViewById(R.id.phoneNumber)
        tokenizeResult = findViewById(R.id.tokenizeResult)

        subscribeToEvents()
    }

    private fun subscribeToEvents() {
        nameElement.addChangeEventListener {
            println("Change event received: $it")
        }

        nameElement.addFocusEventListener {
            println("Element gained focus")
        }

        nameElement.addBlurEventListener {
            println("Element lost focus")
        }
    }

    fun setText(button: View) {
        assert(button.id == R.id.setTextButton)

        nameElement.text = "Manually Set Name"
        phoneNumberElement.text = "Manually Set Phone"
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
        var expirationTimestamp = Instant.now().plus(5, ChronoUnit.MINUTES).toString()

        runBlocking {
            val tokenizeResponse = bt.tokenize(object {
                val type = "token"
                val data = object {
                    val myProp = "My Value"
                    val name = nameElement
                    val phoneNumber = phoneNumberElement
                }
                val expires_at = expirationTimestamp
            })

            val gson = GsonBuilder().setPrettyPrinting().create()

            tokenizeResult.text = gson.toJson(tokenizeResponse)
        }
    }
}