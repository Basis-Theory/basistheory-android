package com.basistheory.android.example.view.virtual_card

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.basistheory.android.example.databinding.FragmentVirtualCardBinding

class VirtualCardFragment : Fragment() {
    private val binding: FragmentVirtualCardBinding by lazy {
        FragmentVirtualCardBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding.lifecycleOwner = this

        binding.readonlyCardNumber.setValueRef(binding.cardNumber)
        binding.readonlyExpirationDate.setValueRef(binding.expirationDate)

        return binding.root
    }
}
