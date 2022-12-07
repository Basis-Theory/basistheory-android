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

    private var _binding: FragmentSocialSecurityNumberBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSocialSecurityNumberBinding.inflate(inflater, container, false)
        val root: View = binding.root

        socialSecurityNumberElement = root.findViewById(R.id.socialSecurityNumber)

        tokenizeResult = root.findViewById(R.id.tokenizeResult)

        _binding?.tokenizeButton?.setOnClickListener { submit(it) }
        _binding?.setTextButton?.setOnClickListener { setText(it) }

        return root
    }


   fun setText(button: View) {
        assert(button.id == R.id.setTextButton)

       socialSecurityNumberElement.setText("234567890")
    }

    fun submit(button: View) {
        assert(button.id == R.id.tokenizeButton)

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
                    val socialSecurityNumber = socialSecurityNumberElement
                }
                val expires_at = expirationTimestamp
            })

            tokenizeResult.text = tokenizeResponse.prettyPrintJson()
        }
    }
}


