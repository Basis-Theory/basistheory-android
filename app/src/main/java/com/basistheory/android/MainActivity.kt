package com.basistheory.android

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.basistheory.Token
import com.google.gson.GsonBuilder
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        secureTextElement.addTextChangedListener(object: TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                println(p0)
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                println(p0)
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//                println(p0)
//            }
//        })
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