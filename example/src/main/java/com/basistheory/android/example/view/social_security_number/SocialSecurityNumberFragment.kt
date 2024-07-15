package com.basistheory.android.example.view.social_security_number

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.basistheory.android.example.databinding.FragmentSocialSecurityNumberBinding
import com.basistheory.android.example.util.tokenExpirationTimestamp
import com.basistheory.android.example.viewmodel.ApiViewModel
import com.basistheory.android.model.InputType

class SocialSecurityNumberFragment : Fragment() {
    private val binding: FragmentSocialSecurityNumberBinding by lazy {
        FragmentSocialSecurityNumberBinding.inflate(layoutInflater)
    }
    private val viewModel: ApiViewModel by viewModels()

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

        binding.socialSecurityNumber.inputType = InputType.NUMBER
        binding.socialSecurityNumber.gravity = Gravity.CENTER

        println((binding.socialSecurityNumber.getChildAt(0) as AppCompatEditText).text)

        return binding.root
    }

    private fun autofill() {
        binding.socialSecurityNumber.setText("234567890")
    }

    private fun tokenize() =
        viewModel.tokenize(
            object {
                val type = "social_security_number"
                val data = binding.socialSecurityNumber
                val expires_at = tokenExpirationTimestamp()
            }).observe(viewLifecycleOwner) {}
}
