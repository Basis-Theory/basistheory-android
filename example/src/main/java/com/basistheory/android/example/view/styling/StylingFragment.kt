package com.basistheory.android.example.view.styling

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.core.content.res.ResourcesCompat.getFont
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.basistheory.android.example.R
import com.basistheory.android.example.databinding.FragmentCardBinding
import com.basistheory.android.example.databinding.FragmentStylingBinding
import com.basistheory.android.example.util.tokenExpirationTimestamp
import com.basistheory.android.example.viewmodel.CardFragmentViewModel

class StylingFragment : Fragment() {
    private val binding: FragmentStylingBinding by lazy {
        FragmentStylingBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding.lifecycleOwner = this

        binding.resetButton.setOnClickListener { reset() }
        binding.style1Button.setOnClickListener { applyStyle1() }
        binding.style2Button.setOnClickListener { applyStyle2() }

        return binding.root
    }

    private fun reset() {
        binding.name.apply {
            removeDefaultStyles = false
            background = null
            textColor = Color.BLACK
            typeface = null
        }
        binding.socialSecurityNumber.apply {
            removeDefaultStyles = false
            background = null
            textColor = Color.BLACK
            typeface = null
        }
    }

    private fun applyStyle1() {
        binding.name.apply {
            removeDefaultStyles = true
            background = getDrawable(resources, R.drawable.rounded_edit_text, null)
            textColor = getColor(resources, R.color.primary, null)
            typeface = getFont(requireContext(), R.font.spacemono_bolditalic)
        }
        binding.socialSecurityNumber.apply {
            removeDefaultStyles = true
            background = getDrawable(resources, R.drawable.rounded_edit_text, null)
            textColor = getColor(resources, R.color.primary, null)
            typeface = getFont(requireContext(), R.font.spacemono_bolditalic)
        }
    }

    private fun applyStyle2() {
        binding.name.apply {
            removeDefaultStyles = true
            background = ColorDrawable(Color.DKGRAY)
            textColor = Color.WHITE
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        }
        binding.socialSecurityNumber.apply {
            removeDefaultStyles = true
            background = ColorDrawable(Color.DKGRAY)
            textColor = Color.WHITE
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        }
    }
}
