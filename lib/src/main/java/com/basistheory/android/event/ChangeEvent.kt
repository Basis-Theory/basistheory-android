package com.basistheory.android.event

data class ChangeEvent(
    val isComplete: Boolean,
    val isEmpty: Boolean,
    val isValid: Boolean,
    val isMaskSatisfied: Boolean,
    val details: List<EventDetails> = mutableListOf()
)

data class EventDetails(
    val type: String,
    val message: String
) {
    companion object {
        const val CardBrand = "cardBrand"
        const val CardBin = "cardBin"
        const val CardLast4 = "cardLast4"
    }
}