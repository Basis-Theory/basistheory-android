package com.basistheory.android.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.basistheory.android.example.BuildConfig
import com.basistheory.android.example.R
import com.basistheory.android.example.util.prettyPrintJson
import com.basistheory.android.service.BasisTheoryElements

open class TokenizeViewModel(application: Application): AndroidViewModel(application) {
    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?>
        get() = _errorMessage

    private val _tokenizeResult = MutableLiveData<String?>(null)
    val tokenizeResult: LiveData<String?>
        get() = _tokenizeResult

    fun tokenize(payload: Any): LiveData<Any> = liveData {
        _errorMessage.value = null
        _tokenizeResult.value = null

        val bt = BasisTheoryElements.builder()
            .apiUrl(BuildConfig.BASIS_THEORY_API_URL)
            .apiKey(BuildConfig.BASIS_THEORY_API_KEY)
            .build()

        runCatching {
            bt.tokenize(payload)
        }.fold(
            onSuccess = {
                _tokenizeResult.value = it.prettyPrintJson()
                emit(it)
            },
            onFailure = {
                _errorMessage.value = getApplication<Application>()
                    .resources
                    .getString(R.string.tokenize_error, it)
            }
        )
    }
}