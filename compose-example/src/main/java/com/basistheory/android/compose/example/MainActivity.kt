package com.basistheory.android.compose.example

import android.graphics.Color
import android.os.Bundle
import android.view.View
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.basistheory.android.compose.example.ui.theme.TextElements
import com.basistheory.android.model.KeyboardType
import com.basistheory.android.service.BasisTheoryElements
import com.basistheory.android.view.TextElement
import com.basistheory.android.view.mask.ElementMask
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    private val bt = BasisTheoryElements.builder()
        .apiKey("key_7jc18XCxYYozHbepjpWHqU")
        .build()

    private lateinit var textElement: TextElement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // A surface container using the 'background' color from the theme
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
                    TextElement(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                        )
                        setPadding(5, 5, 5, 5)
                        hint = "Social Security Number"
                        textColor = Color.GRAY
                        removeDefaultStyles = true
                        keyboardType = KeyboardType.NUMBER
                        mask = ElementMask("###-##-####")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                update = {
                    textElement = it
                    it.addChangeEventListener { e -> println(e) }
                }
            )

            Button(onClick = {
                val tokenizeResponse = runBlocking {
                    bt.tokenize(object {
                        val type = "token"
                        val data = textElement
                    })
                }

                println(tokenizeResponse)
            }) {
                Text(text = "Tokenize")
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        TextElements {
            ShowForm()
        }
    }
}