package com.basistheory.android.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.basistheory.android.event.ChangeEvent
import com.basistheory.android.example.BuildConfig
import com.basistheory.android.example.R
import com.basistheory.android.example.util.prettyPrintJson
import com.basistheory.android.service.BasisTheoryElements

class CardFragmentViewModel(application: Application) : TokenizeViewModel(application) {
    val cardNumberState = MutableLiveData(ElementState())
    val cardExpirationState = MutableLiveData(ElementState())
    val cardCvcState = MutableLiveData(ElementState())

    val canTokenize: MediatorLiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(cardNumberState) {
            value = coalesce(it, cardExpirationState.value, cardCvcState.value)
        }
        addSource(cardExpirationState) {
            value = coalesce(cardNumberState.value, it, cardCvcState.value)
        }
        addSource(cardCvcState) {
            value = coalesce(cardNumberState.value, cardExpirationState.value, it)
        }
    }

    private fun coalesce(vararg states: ElementState?): Boolean =
        states.all { it != null && it.canTokenize }
}

data class ElementState(
    val isComplete: Boolean = false,
    val isValid: Boolean = true
) {
    val canTokenize
        get() = isComplete && isValid

    companion object {
        fun from(e: ChangeEvent) =
            ElementState(e.isComplete, e.isValid)
    }
}
