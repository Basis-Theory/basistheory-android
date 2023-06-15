package com.basistheory.android.example.view.dual_write

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.basistheory.android.example.databinding.FragmentDualWriteBinding
import com.basistheory.android.example.viewmodel.CardFragmentViewModel
import com.basistheory.android.service.HttpMethod

class DualWriteFragment : Fragment() {
    private val binding: FragmentDualWriteBinding by lazy {
        FragmentDualWriteBinding.inflate(layoutInflater)
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

        binding.cvc.cardNumberElement = binding.cardNumber

        binding.postButton.setOnClickListener { createPaymentMethod() }
        binding.autofillButton.setOnClickListener { autofill() }

        setValidationListeners()

        return binding.root
    }


    private fun autofill() {
        binding.cardNumber.setText("4242424242424242")
        binding.expirationDate.setText("12/25")
        binding.cvc.setText("123")
    }

    private fun createPaymentMethod() = viewModel.client.post(
        "https://api.stripe.com/v1/payment_methods", headers = mapOf(
            "Authorization" to "Bearer {{ STRIPE'S API KEY}}",
            "Content-Type" to "application/x-www-form-urlencoded"
        ), object {
            val type = "card"
            val billing_details = object {
                val name = "Peter Panda"
            }
            val card = object {
                val number = binding.cardNumber
                val exp_month = binding.expirationDate.month()
                val exp_year = binding.expirationDate.year()
                val cvc = binding.cvc
            }
        }
    ).observe(viewLifecycleOwner) {}

    /**
     * demonstrates how an application could potentially wire up custom validation behaviors
     */
    private fun setValidationListeners() {
        binding.cardNumber.addChangeEventListener {
            viewModel.cardNumber.observe(it)
        }

        binding.expirationDate.addChangeEventListener {
            viewModel.cardExpiration.observe(it)
        }

        binding.cvc.addChangeEventListener {
            viewModel.cardCvc.observe(it)
        }
    }
}
