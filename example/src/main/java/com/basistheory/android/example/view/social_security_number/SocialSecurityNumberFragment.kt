package com.basistheory.android.example.view.social_security_number

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.basistheory.android.example.BuildConfig
import com.basistheory.android.example.R
import com.basistheory.android.example.databinding.FragmentSocialSecurityNumberBinding
import com.basistheory.android.example.util.prettyPrintJson
import com.basistheory.android.service.BasisTheoryElements
import com.basistheory.android.view.TextElement
import kotlinx.coroutines.runBlocking
import org.threeten.bp.Instant
import org.threeten.bp.temporal.ChronoUnit

class SocialSecurityNumberFragment : Fragment() {
    private lateinit var socialSecurityNumberElement: TextElement
    private lateinit var tokenizeResult: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSocialSecurityNumberBinding.inflate(inflater, container, false)

        socialSecurityNumberElement = binding.root.findViewById(R.id.socialSecurityNumber)

        tokenizeResult = binding.root.findViewById(R.id.tokenize_result)

        binding.tokenizeButton.setOnClickListener { tokenize() }
        binding.autofillButton.setOnClickListener { autofill() }

        return binding.root
    }


   private fun autofill() {
       socialSecurityNumberElement.setText("234567890")
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
                val type = "social_security_number"
                val data = socialSecurityNumberElement
                val expires_at = expirationTimestamp
            })

            tokenizeResult.text = tokenizeResponse.prettyPrintJson()
        }
    }
}


