package com.basistheory.android.example.viewmodel

import android.app.Application
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.basistheory.android.event.ChangeEvent

class CardFragmentViewModel(application: Application) : ApiViewModel(application) {
    val cardNumber = ElementViewModel()
    val cardExpiration = ElementViewModel()
    val cardCvc = ElementViewModel()

    val canSubmit: MediatorLiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(cardNumber.isComplete) {
            value = coalesce(it, cardExpiration.isComplete.value, cardCvc.isComplete.value)
        }
        addSource(cardExpiration.isComplete) {
            value = coalesce(cardNumber.isComplete.value, it, cardCvc.isComplete.value)
        }
        addSource(cardCvc.isComplete) {
            value = coalesce(cardNumber.isComplete.value, cardExpiration.isComplete.value, it)
        }
    }

    private fun coalesce(vararg states: Boolean?): Boolean =
        states.all { it == true }
}

class ElementViewModel {
    val isInvalid = MutableLiveData(false)
    val isComplete = MutableLiveData(false)

    fun observe(e: ChangeEvent) {
        isInvalid.value = e.isMaskSatisfied && !e.isValid
        isComplete.value = e.isComplete
    }
}
