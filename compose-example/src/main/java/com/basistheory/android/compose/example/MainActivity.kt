package com.basistheory.android.compose.example

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.basistheory.android.compose.example.util.tokenExpirationTimestamp
import com.basistheory.android.example.util.prettyPrintJson
import com.basistheory.android.service.BasisTheoryElements
import com.basistheory.android.view.CardExpirationDateElement
import com.basistheory.android.view.CardNumberElement
import com.basistheory.android.view.CardVerificationCodeElement
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    private val bt = BasisTheoryElements.builder()
        .apiKey("key_7jc18XCxYYozHbepjpWHqU")
        .build()

    private lateinit var cardNumberElement: CardNumberElement

    private lateinit var cardExpirationDateElement: CardExpirationDateElement

    private lateinit var cvcElement: CardVerificationCodeElement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                ShowForm()
            }
        }
    }

    @Composable
    fun ShowForm() {
        Column(
            modifier = Modifier
                .padding(10.dp, 10.dp, 10.dp, 10.dp)
        ) {
            AndroidView(
                factory = { context ->
                    CardNumberElement(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                        )
                        setPadding(0, 0, 0, 0)
                        hint = "Card Number"
                        removeDefaultStyles = true
                        textColor = Color.LTGRAY
                        hintTextColor = Color.LTGRAY
                        background =
                            ContextCompat.getDrawable(context, R.drawable.underlined_edit_text)
                        setDrawables(0, 0, R.drawable.card, 0)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                update = {
                    cardNumberElement = it
                    it.addChangeEventListener { e ->
                        run {
                            it.textColor =
                                if (e.isMaskSatisfied && !e.isValid) Color.RED else Color.GRAY
                        }
                    }
                }
            )

            AndroidView(
                factory = { context ->
                    CardExpirationDateElement(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                        )
                        setPadding(0, 0, 0, 0)
                        hint = "MM/YY"
                        removeDefaultStyles = true
                        textColor = Color.LTGRAY
                        hintTextColor = Color.LTGRAY
                        background =
                            ContextCompat.getDrawable(context, R.drawable.underlined_edit_text)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                update = {
                    cardExpirationDateElement = it
                    it.addChangeEventListener { e ->
                        run {
                            it.textColor =
                                if (e.isMaskSatisfied && !e.isValid) Color.RED else Color.GRAY
                        }
                    }
                }
            )

            AndroidView(
                factory = { context ->
                    CardVerificationCodeElement(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                        )
                        setPadding(0, 0, 0, 0)
                        hint = "CVC"
                        removeDefaultStyles = true
                        textColor = Color.LTGRAY
                        hintTextColor = Color.LTGRAY
                        background =
                            ContextCompat.getDrawable(context, R.drawable.underlined_edit_text)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                update = {
                    cvcElement = it
                    cvcElement.cardNumberElement = cardNumberElement
                    it.addChangeEventListener { e ->
                        run {
                            it.textColor =
                                if (e.isMaskSatisfied && !e.isValid) Color.RED else Color.GRAY
                        }
                    }
                }
            )

            Button(
                onClick = {
                    val tokenizeResponse = runBlocking {
                        bt.tokenize(object {
                            val type = "card"
                            val data = object {
                                val number = cardNumberElement
                                val expiration_month = cardExpirationDateElement.month()
                                val expiration_year = cardExpirationDateElement.year()
                                val cvc = cvcElement
                            }
                            val expires_at = tokenExpirationTimestamp()
                        }).prettyPrintJson()
                    }

                    println(tokenizeResponse)
                },
                modifier = Modifier.padding(start = 5.dp, top = 10.dp)
            ) {
                Text(text = "Tokenize")
            }
        }
    }
}