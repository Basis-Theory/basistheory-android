package com.basistheory.android.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.basistheory.Token
import com.basistheory.android.example.BuildConfig
import com.basistheory.android.example.R
import com.basistheory.android.example.util.prettyPrintJson
import com.basistheory.android.service.BasisTheoryElements
import com.basistheory.android.service.ProxyRequest

open class ApiViewModel(application: Application): AndroidViewModel(application) {
    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?>
        get() = _errorMessage

    private val _result = MutableLiveData<String?>(null)
    val result: LiveData<String?>
        get() = _result

    private val _proxyResult = MutableLiveData<String?>(null)
    val proxyResult: LiveData<String?>
        get() = _proxyResult

    private val bt = BasisTheoryElements.builder()
        .apiUrl(BuildConfig.BASIS_THEORY_API_URL)
        .apiKey(BuildConfig.BASIS_THEORY_API_KEY)
        .build()

    fun tokenize(payload: Any): LiveData<Any> = liveData {
        _errorMessage.value = null
        _result.value = null

        runCatching {
            bt.tokenize(payload)
        }.fold(
            onSuccess = {
                _result.value = it.prettyPrintJson()
                emit(it)
            },
            onFailure = {
                _errorMessage.value = getApplication<Application>()
                    .resources
                    .getString(R.string.tokenize_error, it)
            }
        )
    }

    fun proxy(proxyRequest: ProxyRequest): LiveData<Any> = liveData {
        _errorMessage.value = null
        _proxyResult.value = null

        runCatching {
            bt.proxy.post(proxyRequest)
        }.fold(
            onSuccess = {
                _proxyResult.value = it?.prettyPrintJson()
                if (it != null) {
                    emit(it)
                }
            },
            onFailure = {
                _errorMessage.value = getApplication<Application>()
                    .resources
                    .getString(R.string.proxy_error, it)
            }
        )
    }

    fun getToken(id: String): LiveData<Token> = liveData {
        _errorMessage.value = null
        _result.value = null

        runCatching {
            bt.getToken(id)
        }.fold(
            onSuccess = {
                _result.value = it.prettyPrintJson()
                emit(it)
            },
            onFailure = {
                _errorMessage.value = getApplication<Application>()
                    .resources
                    .getString(R.string.get_token_error, it)
            }
        )
    }
}