package com.basistheory.android.example.view.card

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.basistheory.android.example.BuildConfig
import com.basistheory.android.example.R
import com.basistheory.android.example.databinding.FragmentCardBinding
import com.basistheory.android.example.util.prettyPrintJson
import com.basistheory.android.service.BasisTheoryElements
import com.basistheory.android.view.CardExpirationDateElement
import com.basistheory.android.view.CardNumberElement
import com.basistheory.android.view.CardVerificationCodeElement
import kotlinx.coroutines.runBlocking
import org.threeten.bp.Instant
import org.threeten.bp.temporal.ChronoUnit

class CardFragment : Fragment() {
    private lateinit var cardNumberElement: CardNumberElement
    private lateinit var cardExpirationDateElement: CardExpirationDateElement
    private lateinit var cvcElement: CardVerificationCodeElement

    private lateinit var tokenizeResult: TextView
    private lateinit var tokenizeButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCardBinding.inflate(inflater, container, false)

        cardNumberElement = binding.cardNumber
        cardExpirationDateElement = binding.cardExpiration
        cvcElement = binding.cvc

        tokenizeResult = binding.tokenizeResult
        tokenizeButton = binding.tokenizeButton

        binding.tokenizeButton.setOnClickListener { tokenize() }
        binding.autofillButton.setOnClickListener { autofill() }

        setValidationListeners()

        return binding.root
    }

    /**
     * demonstrates how an application could wire up custom validation behaviors
     */
    private fun setValidationListeners() {
        cardNumberElement.addChangeEventListener {
            println(it)

            if (!it.isValid && it.isComplete) {
                cardNumberElement.textColor = Color.RED
                tokenizeButton.isEnabled = false
            } else {
                cardNumberElement.textColor = ResourcesCompat.getColor(resources, R.color.gray_800, null)
                tokenizeButton.isEnabled = true
            }
        }
    }

    private fun autofill() {
        cardNumberElement.setText("4242424242424242")
        cardExpirationDateElement.setText("12/25")
        cvcElement.setText("123")
    }

    private fun tokenize() {
        val bt = BasisTheoryElements.builder()
            .apiUrl(BuildConfig.BASIS_THEORY_API_URL)
            .apiKey(BuildConfig.BASIS_THEORY_API_KEY)
            .build()

        val expirationTimestamp = Instant.now()
            .plus(5, ChronoUnit.MINUTES)
            .toString()

        runBlocking {
            val tokenizeResponse = bt.tokenize(object {
                val type = "card"
                val data = object {
                    val number = cardNumberElement
                    val expiration_month = cardExpirationDateElement.month()
                    val expiration_year = cardExpirationDateElement.year()
                    val cvc = cvcElement
                }
                val expires_at = expirationTimestamp
            })

            tokenizeResult.text = tokenizeResponse.prettyPrintJson()
        }
    }
}


