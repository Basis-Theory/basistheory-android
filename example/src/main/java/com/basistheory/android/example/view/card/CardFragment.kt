package com.basistheory.android.example.view.card

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.basistheory.android.example.databinding.FragmentCardBinding
import com.basistheory.android.example.util.tokenExpirationTimestamp
import com.basistheory.android.example.viewmodel.CardFragmentViewModel
import com.basistheory.android.example.viewmodel.ElementState

class CardFragment : Fragment() {
    private val binding: FragmentCardBinding by lazy {
        FragmentCardBinding.inflate(layoutInflater)
    }
    private val viewModel: CardFragmentViewModel by viewModels()

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

    /**
     * demonstrates how an application could potentially wire up custom validation behaviors
     */
    private fun setValidationListeners() {
        binding.cardNumber.addChangeEventListener {
            viewModel.cardNumberState.value = ElementState.from(it)
        }
        binding.cardExpiration.addChangeEventListener {
            viewModel.cardExpirationState.value = ElementState.from(it)
        }
        binding.cvc.addChangeEventListener {
            viewModel.cardCvcState.value = ElementState.from(it)
        }
    }
}
