package com.basistheory.android.view

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.os.bundleOf
import com.basistheory.android.R
import com.basistheory.android.event.BlurEvent
import com.basistheory.android.event.ChangeEvent
import com.basistheory.android.event.ElementEventListeners
import com.basistheory.android.event.FocusEvent
import com.basistheory.android.model.InputAction
import com.basistheory.android.model.KeyboardType
import com.basistheory.android.view.mask.ElementMask
import com.basistheory.android.view.transform.ElementTransform
import com.basistheory.android.view.validation.ElementValidator


open class TextElement @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val editText = AppCompatEditText(context, null, androidx.appcompat.R.attr.editTextStyle)
    private var defaultBackground = editText.background
    private val eventListeners = ElementEventListeners()
    private var isInternalChange: Boolean = false

    internal var inputAction: InputAction = InputAction.INSERT

    init {
        editText.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        super.addView(editText)

        // wires up attributes declared in the xml layout with properties on this element
        context.theme.obtainStyledAttributes(attrs, R.styleable.TextElement, defStyleAttr, 0)
            .apply {
                try {
                    textColor = getColor(R.styleable.TextElement_textColor, Color.BLACK)
                    hint = getString(R.styleable.TextElement_hint)
                    removeDefaultStyles =
                        getBoolean(R.styleable.TextElement_removeDefaultStyles, false)
                    mask = getString(R.styleable.TextElement_mask)?.let { ElementMask(it) }
                    keyboardType = KeyboardType.fromInt(
                        getInt(
                            R.styleable.TextElement_keyboardType,
                            KeyboardType.TEXT.inputType
                        )
                    )
                    setText(getString(R.styleable.TextElement_text))
                } finally {
                    recycle()
                }
            }

        subscribeToInputEvents()
    }

    // this MUST be internal to prevent host apps from accessing the raw input values
    internal fun getText(): String? =
        editText.text?.toString().let {
            transform?.apply(it) ?: it
        }

    fun setText(value: String?) =
        editText.setText(value)

    var mask: ElementMask? = null

    var transform: ElementTransform? = null

    var validator: ElementValidator? = null

    var textColor: Int
        get() = editText.currentTextColor
        set(value) = editText.setTextColor(value)

    var hint: CharSequence?
        get() = editText.hint
        set(value) {
            editText.hint = value
        }

    var keyboardType: KeyboardType
        get() = KeyboardType.fromInt(editText.inputType)
        set(value) {
            editText.inputType = value.inputType
        }

    var removeDefaultStyles: Boolean
        get() = editText.background == null
        set(value) {
            editText.background = if (value) null else defaultBackground
        }

    fun addChangeEventListener(listener: (ChangeEvent) -> Unit) {
        eventListeners.change.add(listener)
    }

    fun addFocusEventListener(listener: (FocusEvent) -> Unit) {
        eventListeners.focus.add(listener)
    }

    fun addBlurEventListener(listener: (BlurEvent) -> Unit) {
        eventListeners.blur.add(listener)
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection? {
        return editText.onCreateInputConnection(outAttrs)
    }

    override fun onSaveInstanceState(): Parcelable {
        return bundleOf(
            STATE_SUPER to super.onSaveInstanceState(),
            STATE_INPUT to editText.onSaveInstanceState()
        )
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            editText.onRestoreInstanceState(state.getParcelable(STATE_INPUT))
            super.onRestoreInstanceState(state.getParcelable(STATE_SUPER))
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    protected open fun beforeTextChanged(value: String?): String? = value

    protected open fun createElementChangeEvent(
        value: String?,
        isComplete: Boolean,
        isEmpty: Boolean,
        isValid: Boolean
    ): ChangeEvent =
        ChangeEvent(
            isComplete,
            isEmpty,
            isValid,
            mutableListOf()
        )

    private fun subscribeToInputEvents() {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                value: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                value: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (isInternalChange) return

                inputAction =
                    if (before > 0 && count == 0) InputAction.DELETE
                    else InputAction.INSERT
            }

            override fun afterTextChanged(editable: Editable?) {
                afterTextChangedHandler(editable)
            }
        })

        editText.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                eventListeners.focus.forEach { it(FocusEvent()) }
            else
                eventListeners.blur.forEach { it(BlurEvent()) }
        }
    }

    private fun afterTextChangedHandler(editable: Editable?) {
        if (isInternalChange) return

        val originalValue = editable?.toString()
        val transformedValue = beforeTextChanged(originalValue)
            .let { mask?.evaluate(it, inputAction) ?: it }

        if (originalValue != transformedValue)
            applyInternalChange(transformedValue)

        publishChangeEvent(editable)
    }

    private fun applyInternalChange(value: String?) {
        val editable = editText.editableText
        val originalFilters = editable.filters

        isInternalChange = true

        // disable filters on the underlying input applied by the input/keyboard type
        editable.filters = emptyArray()
        editable.replace(0, editable.length, value)
        editable.filters = originalFilters

        isInternalChange = false
    }

    private fun publishChangeEvent(editable: Editable?) {
        val event = createElementChangeEvent(
            getText(),
            mask?.isComplete(editable?.toString()) ?: false,
            editable?.isEmpty() ?: false,
            validator?.validate(getText()) ?: true
        )

        eventListeners.change.forEach {
            it(event)
        }
    }

    internal companion object {
        private const val STATE_SUPER = "state_super"
        private const val STATE_INPUT = "state_input"
    }
}
