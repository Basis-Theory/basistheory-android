package com.basistheory.android

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.basistheory.Token
import com.google.gson.GsonBuilder
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var secureTextElement: TextElement
    private lateinit var tokenizeResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        secureTextElement = findViewById(R.id.secureTextElement)
        tokenizeResult = findViewById(R.id.tokenizeResult)

        subscribeToEvents()
    }

    private fun subscribeToEvents() {
        secureTextElement.addChangeEventListener {
            println("Change event received: $it")
        }

        secureTextElement.addFocusEventListener {
            println("Element gained focus")
        }

        secureTextElement.addBlurEventListener {
            println("Element lost focus")
        }
    }

    fun submit(view: View) {
        val secureTextElement = findViewById<TextElement>(R.id.secureTextElement)
        val tokenizeResult = findViewById<TextView>(R.id.tokenizeResult)

        try {
            val myExecutor = Executors.newSingleThreadExecutor()

            myExecutor.execute {
                val tokenizeResponse = BasisTheoryElements.tokenize(object {
                    val type = "token"
                    val data = secureTextElement.getValue()?.toString()
                })

                val gson = GsonBuilder().setPrettyPrinting().create()

                Handler(Looper.getMainLooper()).post {
                    tokenizeResult.text = gson.toJson(tokenizeResponse)
                }
            }
        } catch(e: Throwable) {
            println(e)
            throw e
        }
    }
}