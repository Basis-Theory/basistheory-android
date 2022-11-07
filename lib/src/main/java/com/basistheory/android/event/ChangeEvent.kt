package com.basistheory.android.event

data class ChangeEvent(
    val complete: Boolean,
    val empty: Boolean,
    val errors: List<ElementEventError>
)