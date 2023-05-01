package com.basistheory.android.example.view.custom_form

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.basistheory.android.example.databinding.FragmentCustomFormBinding
import com.basistheory.android.example.util.tokenExpirationTimestamp
import com.basistheory.android.example.viewmodel.ApiViewModel
import com.basistheory.android.model.InputType
import com.basistheory.android.view.mask.ElementMask

class CustomFormFragment : Fragment() {
    private val binding: FragmentCustomFormBinding by lazy {
        FragmentCustomFormBinding.inflate(layoutInflater)
    }
    private val viewModel: ApiViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val digitRegex = Regex("""\d""")
        val charRegex = Regex("""[A-Za-z]""")

        // illustrates that inputType can be set programmatically (or in xml)
        binding.phoneNumber.inputType = InputType.NUMBER
        binding.phoneNumber.mask = ElementMask(
            listOf(
                "+",
                "1",
                "(",
                digitRegex,
                digitRegex,
                digitRegex,
                ")",
                " ",
                digitRegex,
                digitRegex,
                digitRegex,
                "-",
                digitRegex,
                digitRegex,
                digitRegex,
                digitRegex
            )
        )

        binding.orderNumber.mask = ElementMask(
            listOf(charRegex, charRegex, charRegex, "-", digitRegex, digitRegex, digitRegex)
        )

        binding.tokenizeButton.setOnClickListener { tokenize() }
        binding.autofillButton.setOnClickListener { autofill() }

        return binding.root
    }

    private fun autofill() {
        binding.name.setText("John Doe")
        binding.phoneNumber.setText("2345678900")
        binding.orderNumber.setText("ABC123")
        binding.password.setText("secret password 123")
        binding.pin.setText("1234")
    }

    private fun tokenize() = viewModel.tokenize(object {
        val type = "token"
        val data = object {
            val name = binding.name
            val phoneNumber = binding.phoneNumber
            val orderNumber = binding.orderNumber
            val password = binding.password
            val pin = binding.pin
        }
        val expires_at = tokenExpirationTimestamp()
    }).observe(viewLifecycleOwner) {}
}


