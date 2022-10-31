package com.basistheory.example

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.basistheory.android.BasisTheoryElements
import com.basistheory.android.TextElement
import com.basistheory.example.utils.toIsoString
import com.google.gson.GsonBuilder
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.time.temporal.ChronoUnit

class MainActivity : AppCompatActivity() {
    private lateinit var nameElement: TextElement
    private lateinit var phoneNumberElement: TextElement
    private lateinit var tokenizeResult: TextView

    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }

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
            .apiUrl(dotenv["BASIS_THEORY_API_URL"])
            .apiKey(dotenv["BASIS_THEORY_API_KEY"])
            .build()

        runBlocking {
            val tokenizeResponse = bt.tokenize(object {
                val type = "token"
                val data = object {
                    val myProp = "My Value"
                    val name = nameElement
                    val phoneNumber = phoneNumberElement
                }
                val expires_at = Instant.now().plus(5, ChronoUnit.MINUTES).toIsoString()
            })

            val gson = GsonBuilder().setPrettyPrinting().create()

            tokenizeResult.text = gson.toJson(tokenizeResponse)
        }
    }
}