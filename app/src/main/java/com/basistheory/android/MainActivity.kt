package com.basistheory.android

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import java.util.concurrent.Executors

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
        assert(button.id == R.id.setText)

        nameElement.text = "Tom Cruise"
        phoneNumberElement.text = "555-123-4567"
    }

    fun submit(button: View) {
        assert(button.id == R.id.submitButton)

        try {
            val myExecutor = Executors.newSingleThreadExecutor()

            myExecutor.execute {
                val tokenizeResponse = BasisTheoryElements.tokenize(object {
                    val type = "token"
                    val data = object {
                        val myProp = "My Value"
                        val name = nameElement
                        val phoneNumber = phoneNumberElement
                    }
                })

                val gson = GsonBuilder().setPrettyPrinting().create()

                Handler(Looper.getMainLooper()).post {
                    tokenizeResult.text = gson.toJson(tokenizeResponse)
                }
            }
        } catch (e: Throwable) {
            println(e)
            throw e
        }
    }
}