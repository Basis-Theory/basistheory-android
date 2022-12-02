package com.basistheory.android.event

data class ChangeEvent(
    val isComplete: Boolean,
    val isEmpty: Boolean,
    val isValid: Boolean
)