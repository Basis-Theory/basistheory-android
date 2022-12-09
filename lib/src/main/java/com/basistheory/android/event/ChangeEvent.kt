package com.basistheory.android.event

data class ChangeEvent(
    val isComplete: Boolean,
    val isEmpty: Boolean,
    val isValid: Boolean,
    val details: List<EventDetails> = mutableListOf()
)

data class EventDetails(
    val type: String,
    val message: String
)