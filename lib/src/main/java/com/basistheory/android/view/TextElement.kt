package com.basistheory.android.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
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
import com.basistheory.android.model.ElementValueReference
import com.basistheory.android.model.InputAction
import com.basistheory.android.model.InputType
import com.basistheory.android.view.mask.ElementMask
import com.basistheory.android.view.method.FullyHiddenTransformationMethod
import com.basistheory.android.view.transform.ElementTransform
import com.basistheory.android.view.validation.ElementValidator

@SuppressLint("ResourceType")
open class TextElement @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val _editText =
        AppCompatEditText(context, null, androidx.appcompat.R.attr.editTextStyle)
    private var _defaultBackground = _editText.background
    private val _eventListeners = ElementEventListeners()
    private var _isInternalChange: Boolean = false
    private var _isValid: Boolean = true
    private var _isMaskSatisfied: Boolean = true
    private var _isEmpty: Boolean = true
    private var _inputType: InputType = InputType.TEXT

    internal var inputAction: InputAction = InputAction.INSERT

    init {
        _editText.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        super.addView(_editText)

        val androidAttributes = arrayOf(
            android.R.attr.hint,
            android.R.attr.inputType,
            android.R.attr.enabled,
            android.R.attr.text,
            android.R.attr.textColor,
            android.R.attr.textSize,
            android.R.attr.textColorHint,
            android.R.attr.typeface
        ).toIntArray()

        // wire up standard android attributes
        context.theme.obtainStyledAttributes(attrs, androidAttributes, defStyleAttr, 0)
            .apply {
                try {
                    hint = getString(0)

                    inputType = InputType.fromAndroidAttr(
                        getInt(1, android.text.InputType.TYPE_CLASS_TEXT)
                    )

                    // todo: rename isEditable -> enabled?
                    isEditable = getBoolean(2, true)

                    setText(getString(3))

                    textColor = getColor(4, Color.BLACK)

                    textSize = getDimension(5, 16f * resources.displayMetrics.scaledDensity)

                    // todo: rename hintTextColor -> textColorHint
                    hintTextColor = getColor(6, Color.LTGRAY)

                    typeface = resolveTypeface(
                        getInt(7, 0),
                        defStyleAttr
                    )
                } finally {
                    recycle()
                }
            }

        // wire up custom attributes
        context.theme.obtainStyledAttributes(attrs, R.styleable.TextElement, defStyleAttr, 0)
            .apply {
                try {
                    // map standard android attributes
                    hint = getString(R.styleable.TextElement_android_hint)

                    inputType = InputType.fromAndroidAttr(
                        getInt(
                            R.styleable.TextElement_android_inputType,
                            android.text.InputType.TYPE_CLASS_TEXT
                        )
                    )

                    // todo: rename isEditable -> enabled?
                    isEditable = getBoolean(R.styleable.TextElement_android_enabled, true)

                    setText(getString(R.styleable.TextElement_android_text))

                    textColor = getColor(R.styleable.TextElement_android_textColor, Color.BLACK)

                    textSize = getDimension(R.styleable.TextElement_android_textSize, 16f * resources.displayMetrics.scaledDensity)

                    // todo: rename hintTextColor -> textColorHint
                    hintTextColor = getColor(R.styleable.TextElement_android_textColorHint, Color.LTGRAY)

                    typeface = resolveTypeface(
                        getInt(R.styleable.TextElement_android_typeface, 0),
                        defStyleAttr
                    )

                    // map custom attributes
                    mask = getString(R.styleable.TextElement_bt_mask)?.let { ElementMask(it) }

                    removeDefaultStyles = getBoolean(
                        R.styleable.TextElement_bt_removeDefaultStyles,
                        true
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

    fun setValueRef(elementValueReference: ElementValueReference) {
        setText(elementValueReference.getValue())
        _editText.requestLayout()
    }

    fun setDrawables(startDrawable: Int, topDrawable: Int, endDrawable: Int, bottomDrawable: Int) {
        _editText.setCompoundDrawablesWithIntrinsicBounds(
            startDrawable,
            topDrawable,
            endDrawable,
            bottomDrawable
        )
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

    var typeface: Typeface?
        get() = _editText.typeface
        set(value) {
            _editText.typeface = value
        }

    var hint: CharSequence?
        get() = _editText.hint
        set(value) {
            _editText.hint = value
        }

    var hintTextColor: Int
        get() = _editText.currentHintTextColor
        set(value) {
            _editText.setHintTextColor(value)
        }

    var inputType: InputType
        get() = _inputType
        set(value) {
            _inputType = value
            _editText.inputType = value.androidInputType

            if (value.isConcealed)
                _editText.transformationMethod = FullyHiddenTransformationMethod()
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

    private fun resolveTypeface(typefaceIndex: Int, style: Int): Typeface? =
        when (typefaceIndex) {
            1 -> Typeface.create(Typeface.SANS_SERIF, style)
            2 -> Typeface.create(Typeface.SERIF, style)
            3 -> Typeface.create(Typeface.MONOSPACE, style)
            else -> Typeface.defaultFromStyle(style)
        }

    internal companion object {
        private const val STATE_SUPER = "state_super"
        private const val STATE_INPUT = "state_input"
    }
}
