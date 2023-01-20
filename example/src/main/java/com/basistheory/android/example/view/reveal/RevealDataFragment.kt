package com.basistheory.android.example.view.reveal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.basistheory.android.example.databinding.FragmentRevealBinding
import com.basistheory.android.example.util.tokenExpirationTimestamp
import com.basistheory.android.example.viewmodel.ProxyViewModel
import com.basistheory.android.model.ElementValueReference
import com.basistheory.android.service.ProxyRequest

class RevealDataFragment : Fragment() {
    private val binding: FragmentRevealBinding by lazy {
        FragmentRevealBinding.inflate(layoutInflater)
    }
    private val viewModel: ProxyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.revealButton.setOnClickListener { reveal() }

        return binding.root
    }

    private fun reveal() {
        val proxyRequest: ProxyRequest = ProxyRequest().apply {
            headers = mapOf(
                "BT-PROXY-URL" to "https://echo.basistheory.com/post",
                "Content-Type" to "application/json"
            )
            body = object {
                val type = "card"
                val data = object {
                    val number = "4242424242424242"
                    val expiration_month = "08"
                    val expiration_year = "26"
                    val cvc = "123"
                }
                val expires_at = tokenExpirationTimestamp()
            }
        }

        viewModel.proxy(proxyRequest).observe(viewLifecycleOwner) {
            binding.revealedData
                .setValueRef(
                    (((it as Map<*, *>)["json"]
                            as Map<*, *>)["data"]
                            as Map<*, *>)["number"]
                            as ElementValueReference
                )
        }
    }
}
