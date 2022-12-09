package com.basistheory.android.example.view.card

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.basistheory.android.example.R
import com.basistheory.android.example.databinding.FragmentCardBinding
import com.basistheory.android.example.util.tokenExpirationTimestamp
import com.basistheory.android.example.viewmodel.TokenizeViewModel

class CardFragment : Fragment() {
    private val binding: FragmentCardBinding by lazy {
        FragmentCardBinding.inflate(layoutInflater)
    }
    private val viewModel: TokenizeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.tokenizeButton.setOnClickListener { tokenize() }
        binding.autofillButton.setOnClickListener { autofill() }

        setValidationListeners()

        return binding.root
    }

    /**
     * demonstrates how an application could wire up custom validation behaviors
     */
    // todo: try to move the valid state tracking into the vm and use data binding for styling
    private fun setValidationListeners() {
        binding.cardNumber.addChangeEventListener {
            if (!it.isValid && it.isComplete) {
                binding.cardNumber.textColor = Color.RED
                binding.tokenizeButton.isEnabled = false
            } else {
                binding.cardNumber.textColor =
                    ResourcesCompat.getColor(resources, R.color.gray_800, null)
                binding.tokenizeButton.isEnabled = true
            }
        }
    }

    private fun autofill() {
        binding.cardNumber.setText("4242424242424242")
        binding.cardExpiration.setText("12/25")
        binding.cvc.setText("123")
    }

    private fun tokenize() =
        viewModel.tokenize(
            object {
                val type = "card"
                val data = object {
                    val number = binding.cardNumber
                    val expiration_month = binding.cardExpiration.month()
                    val expiration_year = binding.cardExpiration.year()
                    val cvc = binding.cvc
                }
                val expires_at = tokenExpirationTimestamp()
            }).observe(viewLifecycleOwner) {}
}
