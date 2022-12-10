package com.basistheory.android.example.viewmodel

import android.app.Application
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.basistheory.android.event.ChangeEvent

class CardFragmentViewModel(application: Application) : TokenizeViewModel(application) {
    val cardNumber = ElementViewModel()
    val cardExpiration = ElementViewModel()
    val cardCvc = ElementViewModel()

    val canSubmit: MediatorLiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(cardNumber.canSubmit) {
            value = coalesce(it, cardExpiration.canSubmit.value, cardCvc.canSubmit.value)
        }
        addSource(cardExpiration.canSubmit) {
            value = coalesce(cardNumber.canSubmit.value, it, cardCvc.canSubmit.value)
        }
        addSource(cardCvc.canSubmit) {
            value = coalesce(cardNumber.canSubmit.value, cardExpiration.canSubmit.value, it)
        }
    }

    private fun coalesce(vararg states: Boolean?): Boolean =
        states.all { it == true }
}

class ElementViewModel {
    val isInvalid = MutableLiveData(false)
    val canSubmit = MutableLiveData(false)

    fun observe(e: ChangeEvent) {
        isInvalid.value = e.isComplete && !e.isValid
        canSubmit.value = e.isComplete && e.isValid
    }
}
