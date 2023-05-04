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
import com.basistheory.android.service.BasisTheoryElements
import com.basistheory.android.view.CardNumberElement
import com.basistheory.android.view.CardVerificationCodeElement
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    private val bt = BasisTheoryElements.builder()
        .apiKey("YOUR_API_KEY")
        .build()

    private lateinit var cardNumberElement: CardNumberElement

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
        Column(modifier = Modifier.padding(10.dp)) {
            AndroidView(
                factory = { context ->
                    CardNumberElement(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                        )
                        setPadding(5, 5, 5, 5)
                        hint = "Card Number"
                        textColor = Color.GRAY
                        removeDefaultStyles = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                update = {
                    cardNumberElement = it
                    it.addChangeEventListener { e ->
                        run {
                            println(e)
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
                        setPadding(5, 5, 5, 5)
                        hint = "CVC"
                        textColor = Color.GRAY
                        removeDefaultStyles = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                update = {
                    cvcElement = it
                    cvcElement.cardNumberElement = cardNumberElement
                }
            )

            Button(onClick = {
                val tokenizeResponse = runBlocking {
                    bt.tokenize(object {
                        val type = "card_number"
                        val data = cardNumberElement
                    })
                }

                println(tokenizeResponse)
            }) {
                Text(text = "Tokenize")
            }
        }
    }
}