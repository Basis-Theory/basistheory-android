package com.basistheory.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.basistheory.android.R

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

        println(secureTextElement.getValue())
    }
}