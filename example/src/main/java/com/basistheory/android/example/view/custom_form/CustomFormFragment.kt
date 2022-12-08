package com.basistheory.android.example.view.custom_form

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.basistheory.android.example.BuildConfig
import com.basistheory.android.example.R
import com.basistheory.android.example.databinding.FragmentCustomFormBinding
import com.basistheory.android.example.util.prettyPrintJson
import com.basistheory.android.service.BasisTheoryElements
import com.basistheory.android.model.KeyboardType
import com.basistheory.android.view.TextElement
import kotlinx.coroutines.runBlocking
import org.threeten.bp.Instant
import org.threeten.bp.temporal.ChronoUnit

class CustomFormFragment : Fragment() {
    private lateinit var nameElement: TextElement
    private lateinit var phoneNumberElement: TextElement
    private lateinit var orderNumberElement: TextElement
    private lateinit var tokenizeResult: TextView
    private lateinit var tokenizeButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCustomFormBinding.inflate(inflater, container, false)

        nameElement = binding.root.findViewById(R.id.name)
        phoneNumberElement = binding.root.findViewById(R.id.phoneNumber)
        orderNumberElement = binding.root.findViewById(R.id.orderNumber)
        tokenizeResult = binding.root.findViewById(R.id.tokenize_result)
        tokenizeButton = binding.root.findViewById(R.id.tokenize_button)

        val digitRegex = Regex("""\d""")
        val charRegex = Regex("""[A-Za-z]""")

        phoneNumberElement.keyboardType = KeyboardType.NUMBER // illustrates that it can be set programmatically
        phoneNumberElement.mask = listOf("+", "1", "(", digitRegex,digitRegex,digitRegex, ")", " ", digitRegex, digitRegex, digitRegex, "-", digitRegex, digitRegex , digitRegex, digitRegex )

        orderNumberElement.mask = listOf(charRegex, charRegex, charRegex, "-", digitRegex, digitRegex, digitRegex)

        binding.tokenizeButton.setOnClickListener { tokenize() }
        binding.autofillButton.setOnClickListener { autofill() }

        return binding.root
    }

   private fun autofill() {
       nameElement.setText("John Doe")
       phoneNumberElement.setText("2345678900")
       orderNumberElement.setText("ABC123")
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
                val type = "token"
                val data = object {
                    val name = nameElement
                    val phoneNumber = phoneNumberElement
                    val orderNumber = orderNumberElement
                }
                val expires_at = expirationTimestamp
            })

            tokenizeResult.text = tokenizeResponse.prettyPrintJson()
        }
    }
}


