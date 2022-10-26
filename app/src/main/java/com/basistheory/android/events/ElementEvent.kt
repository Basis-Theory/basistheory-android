package com.basistheory.android.events

data class ChangeEvent(
    val complete: Boolean,
    val empty: Boolean,
    val errors: List<ElementEventError>
)

class FocusEvent
class BlurEvent

data class ElementEventError(
    val targetId: String,
    val type: String
)

class ElementEventListeners {
    val change: MutableList<(ChangeEvent) -> Unit> = mutableListOf()
    val focus: MutableList<(FocusEvent) -> Unit> = mutableListOf()
    val blur: MutableList<(BlurEvent) -> Unit> = mutableListOf()
}
