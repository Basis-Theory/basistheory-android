package com.basistheory.android.view

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
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
    private val _editText = AppCompatEditText(context, null, androidx.appcompat.R.attr.editTextStyle)
    private var _defaultBackground = _editText.background
    private val _eventListeners = ElementEventListeners()
    private var _isInternalChange: Boolean = false
    private var _isValid: Boolean = true
    private var _isMaskSatisfied: Boolean = true
    private var _isEmpty: Boolean = true

    internal var inputAction: InputAction = InputAction.INSERT

    init {
        _editText.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        super.addView(_editText)

        // wires up attributes declared in the xml layout with properties on this element
        context.theme.obtainStyledAttributes(attrs, R.styleable.TextElement, defStyleAttr, 0)
            .apply {
                try {
                    isEditable = getBoolean(
                        R.styleable.TextElement_editable,
                        true
                    )

                    hint = getString(R.styleable.TextElement_hint)

                    keyboardType = KeyboardType.fromInt(
                        getInt(
                            R.styleable.TextElement_keyboardType,
                            KeyboardType.TEXT.inputType
                        )
                    )

                    mask = getString(R.styleable.TextElement_mask)?.let { ElementMask(it) }

                    removeDefaultStyles = getBoolean(
                        R.styleable.TextElement_removeDefaultStyles,
                        true
                    )

                    setText(getString(R.styleable.TextElement_text))

                    textColor = getColor(
                        R.styleable.TextElement_textColor,
                        Color.BLACK
                    )

                    textSize = getDimension(
                        R.styleable.TextElement_textSize,
                        16f * resources.displayMetrics.scaledDensity
                    )
                } finally {
                    recycle()
                }
            }

        subscribeToInputEvents()
    }

    // the following getters MUST be internal to prevent host apps from accessing the raw input values

    internal fun getText(): String? =
        _editText.text?.toString()

    internal fun getTransformedText(): String? =
        _editText.text?.toString().let {
            transform?.apply(it) ?: it
        }

    fun setText(value: String?) =
        _editText.setText(value)

    fun setValueRef(element: TextElement) {
        element.addChangeEventListener {
            setText(element.getText())
            _editText.requestLayout()
        }
    }

    val isComplete: Boolean
        get() = _isMaskSatisfied && _isValid

    val isValid: Boolean
        get() = _isValid

    val isMaskSatisfied: Boolean
        get() = _isMaskSatisfied

    val isEmpty: Boolean
        get() = _isEmpty

    var isEditable: Boolean
        get() = _editText.isEnabled
        set(value) {
            isEnabled = value
            _editText.isEnabled = value
        }

    var mask: ElementMask? = null
        set(value) {
            field = value
            _isMaskSatisfied = mask == null
        }

    var transform: ElementTransform? = null

    var validator: ElementValidator? = null
        set(value) {
            field = value
            _isValid = validator == null
        }

    var textColor: Int
        get() = _editText.currentTextColor
        set(value) = _editText.setTextColor(value)

    var textSize: Float
        get() = _editText.textSize
        set(value) = _editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)

    var hint: CharSequence?
        get() = _editText.hint
        set(value) {
            _editText.hint = value
        }

    var keyboardType: KeyboardType
        get() = KeyboardType.fromInt(_editText.inputType)
        set(value) {
            _editText.inputType = value.inputType
        }

    var removeDefaultStyles: Boolean
        get() = _editText.background == null
        set(value) {
            _editText.background = if (value) null else _defaultBackground
        }

    fun addChangeEventListener(listener: (ChangeEvent) -> Unit) {
        _eventListeners.change.add(listener)
    }

    fun addFocusEventListener(listener: (FocusEvent) -> Unit) {
        _eventListeners.focus.add(listener)
    }

    fun addBlurEventListener(listener: (BlurEvent) -> Unit) {
        _eventListeners.blur.add(listener)
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection? {
        return _editText.onCreateInputConnection(outAttrs)
    }

    override fun onSaveInstanceState(): Parcelable {
        return bundleOf(
            STATE_SUPER to super.onSaveInstanceState(),
            STATE_INPUT to _editText.onSaveInstanceState()
        )
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            _editText.onRestoreInstanceState(state.getParcelable(STATE_INPUT))
            super.onRestoreInstanceState(state.getParcelable(STATE_SUPER))
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    protected open fun beforeTextChanged(value: String?): String? = value

    protected open fun createElementChangeEvent(): ChangeEvent =
        ChangeEvent(
            isComplete,
            isEmpty,
            isValid,
            isMaskSatisfied,
            mutableListOf()
        )

    private fun subscribeToInputEvents() {
        _editText.addTextChangedListener(object : TextWatcher {
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
                if (_isInternalChange) return

                inputAction =
                    if (before > 0 && count == 0) InputAction.DELETE
                    else InputAction.INSERT
            }

            override fun afterTextChanged(editable: Editable?) {
                afterTextChangedHandler(editable)
            }
        })

        _editText.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                _eventListeners.focus.forEach { it(FocusEvent()) }
            else
                _eventListeners.blur.forEach { it(BlurEvent()) }
        }
    }

    private fun afterTextChangedHandler(editable: Editable?) {
        if (_isInternalChange) return

        val originalValue = editable?.toString()
        val transformedValue = beforeTextChanged(originalValue)
            .let { mask?.evaluate(it, inputAction) ?: it }

        if (originalValue != transformedValue)
            applyInternalChange(transformedValue)

        _isValid = validator?.validate(getTransformedText()) ?: true
        _isMaskSatisfied = mask?.isSatisfied(editable?.toString()) ?: true
        _isEmpty = editable?.toString()?.isEmpty() ?: true

        publishChangeEvent()
    }

    private fun applyInternalChange(value: String?) {
        val editable = _editText.editableText
        val originalFilters = editable.filters

        _isInternalChange = true

        // disable filters on the underlying input applied by the input/keyboard type
        editable.filters = emptyArray()
        editable.replace(0, editable.length, value)
        editable.filters = originalFilters

        _isInternalChange = false
    }

    protected fun publishChangeEvent() {
        val event = createElementChangeEvent()

        _eventListeners.change.forEach {
            it(event)
        }
    }

    internal companion object {
        private const val STATE_SUPER = "state_super"
        private const val STATE_INPUT = "state_input"
    }
}
