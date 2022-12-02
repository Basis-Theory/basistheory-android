package com.basistheory.android.example.text_element

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
import com.basistheory.android.example.databinding.FragmentTextElementBinding
import com.basistheory.android.service.BasisTheoryElements
import com.basistheory.android.view.CardNumberElement
import com.basistheory.android.view.KeyboardType
import com.basistheory.android.view.TextElement
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import org.threeten.bp.Instant
import org.threeten.bp.temporal.ChronoUnit

class TextElementFragment : Fragment() {
    private lateinit var cardNumberElement: CardNumberElement
    private lateinit var nameElement: TextElement
    private lateinit var phoneNumberElement: TextElement
    private lateinit var orderNumberElement: TextElement
    private lateinit var tokenizeResult: TextView
    private lateinit var tokenizeButton: Button

    private var _binding: FragmentTextElementBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTextElementBinding.inflate(inflater, container, false)
        val root: View = binding.root

        cardNumberElement = root.findViewById(R.id.cardNumber)
        nameElement = root.findViewById(R.id.name)
        phoneNumberElement = root.findViewById(R.id.phoneNumber)
        orderNumberElement = root.findViewById(R.id.orderNumber)
        tokenizeResult = root.findViewById(R.id.tokenizeResult)
        tokenizeButton = root.findViewById(R.id.tokenizeButton)

        val digitRegex = Regex("""\d""")
        val charRegex = Regex("""[A-Za-z]""")

        phoneNumberElement.keyboardType = KeyboardType.NUMBER // illustrates that it can be set programmatically
        phoneNumberElement.mask = listOf("+", "1", "(", digitRegex,digitRegex,digitRegex, ")", " ", digitRegex, digitRegex, digitRegex, "-", digitRegex, digitRegex , digitRegex, digitRegex )

        orderNumberElement.mask = listOf(charRegex, charRegex, charRegex, "-", digitRegex, digitRegex, digitRegex)

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


        subscribeToEvents()

        return root
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

       cardNumberElement.setText("4242424242424242")
       nameElement.setText("Manually Set Name")
       phoneNumberElement.setText("2345678900")
       orderNumberElement.setText("ABC123")
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
                    val name = nameElement
                    val phoneNumber = phoneNumberElement
                    val orderNumber = orderNumberElement
                }
                val expires_at = expirationTimestamp
            })

            val gson = GsonBuilder().setPrettyPrinting().create()

            tokenizeResult.text = gson.toJson(tokenizeResponse)
        }
    }
}


