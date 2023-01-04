package com.basistheory.android.example.view.readonly_binding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.basistheory.android.example.databinding.FragmentCardBinding
import com.basistheory.android.example.databinding.FragmentReadonlyBindingBinding
import com.basistheory.android.example.util.tokenExpirationTimestamp
import com.basistheory.android.example.viewmodel.CardFragmentViewModel

class ReadonlyBindingFragment : Fragment() {
    private val binding: FragmentReadonlyBindingBinding by lazy {
        FragmentReadonlyBindingBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding.lifecycleOwner = this

        binding.readOnlyCardNumber.elementRef = binding.cardNumber

        binding.autofillButton.setOnClickListener { autofill() }

        return binding.root
    }

    private fun autofill() {
        binding.cardNumber.setText("4242424242424242")
    }
}
