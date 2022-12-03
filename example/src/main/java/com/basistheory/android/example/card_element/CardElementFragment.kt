package com.basistheory.android.example.card_element

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.basistheory.android.example.BuildConfig
import com.basistheory.android.example.R
import com.basistheory.android.example.databinding.FragmentCardElementBinding
import com.basistheory.android.example.databinding.FragmentTextElementBinding
import com.basistheory.android.service.BasisTheoryElements
import com.basistheory.android.view.CardNumberElement
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import org.threeten.bp.Instant
import org.threeten.bp.temporal.ChronoUnit

class CardElementFragment : Fragment() {
    private lateinit var cardNumberElement: CardNumberElement

    private lateinit var tokenizeResult: TextView
    private lateinit var tokenizeButton: Button

    private var _binding: FragmentCardElementBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCardElementBinding.inflate(inflater, container, false)
        val root: View = binding.root

        cardNumberElement = root.findViewById(R.id.cardNumber)

        tokenizeResult = root.findViewById(R.id.tokenizeResult)
        tokenizeButton = root.findViewById(R.id.tokenizeButton)

        cardNumberElement.addChangeEventListener {
            if (!it.isValid && it.isComplete) {
                cardNumberElement.textColor = Color.RED
                tokenizeButton.isEnabled = false
            } else {
                cardNumberElement.textColor = Color.BLACK
                tokenizeButton.isEnabled = true
            }
        }

        _binding?.tokenizeButton?.setOnClickListener { submit(it) }
        _binding?.setTextButton?.setOnClickListener { setText(it) }
        return root
    }

    fun setText(button: View) {
        assert(button.id == R.id.setTextButton)

        cardNumberElement.setText("4242424242424242")
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
        val expirationTimestamp = Instant.now().plus(5, ChronoUnit.MINUTES).toString()

        runBlocking {
            val tokenizeResponse = bt.tokenize(object {
                val type = "token"
                val data = object {
                    val staticProp = "Static Value"
                    val cardNumber = cardNumberElement
                }
                val expires_at = expirationTimestamp
            })

            val gson = GsonBuilder().setPrettyPrinting().create()

            tokenizeResult.text = gson.toJson(tokenizeResponse)
        }
    }
}


