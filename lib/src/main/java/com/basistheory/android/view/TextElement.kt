package com.basistheory.android.view

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatEditText
import com.basistheory.android.R
import com.basistheory.android.event.BlurEvent
import com.basistheory.android.event.ChangeEvent
import com.basistheory.android.event.ElementEventListeners
import com.basistheory.android.event.FocusEvent
import com.basistheory.android.model.KeyboardType
import com.basistheory.android.model.InputAction
import com.basistheory.android.view.mask.Mask

open class TextElement : FrameLayout {
    private var attrs: AttributeSet? = null
    private var defStyleAttr: Int = androidx.appcompat.R.attr.editTextStyle
    private var input: AppCompatEditText = AppCompatEditText(context, attrs, defStyleAttr)
    private var defaultBackground = input.background // todo: find a better way to reference this
    private val eventListeners = ElementEventListeners()
    private var maskValue: Mask? = null

    constructor(context: Context) : super(context) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.attrs = attrs

        initialize()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.attrs = attrs
        this.defStyleAttr = defStyleAttr

        initialize()
    }

    // this MUST be internal to prevent host apps from accessing the raw input values
    internal fun getText(): String? =
        transform(input.text?.toString())

    fun setText(value: String?) =
        input.setText(value)

    internal var validate: (value: String?) -> Boolean =
        { _ -> true }

    internal var transform: (value: String?) -> String? =
        { value -> value }

    var textColor: Int
        get() = input.currentTextColor
        set(value) = input.setTextColor(value)

    var hint: CharSequence?
        get() = input.hint
        set(value) {
            input.hint = value
        }

    var keyboardType: KeyboardType
        get() = KeyboardType.fromInt(input.inputType)
        set(value) {
            input.inputType = value.inputType
        }

    var mask: List<Any>? = null
        set(value) {
            field = value
            maskValue = value?.let { Mask(it) }
        }

    var removeDefaultStyles: Boolean
        get() = input.background == null
        set(value) {
            input.background = if (value) null else defaultBackground
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
        return input.onCreateInputConnection(outAttrs)
    }

    private fun initialize() {
        // wires up attributes declared in the xml layout with properties on this element
        context.theme.obtainStyledAttributes(attrs, R.styleable.TextElement, defStyleAttr, 0)
            .apply {
                try {
                    textColor = getColor(R.styleable.TextElement_textColor, Color.BLACK)
                    hint = getString(R.styleable.TextElement_hint)
                    removeDefaultStyles =
                        getBoolean(R.styleable.TextElement_removeDefaultStyles, false)
                    mask = getString(R.styleable.TextElement_mask)?.split("")
                        ?.filter { it.isNotEmpty() }
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

        input.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        super.addView(input)

        subscribeToInputEvents()
    }

    protected open fun transformUserInput(userInput: String?): String? = userInput

    private fun subscribeToInputEvents() {
        input.addTextChangedListener(object : TextWatcher {
            private var isInternalChange: Boolean = false
            private var inputAction: InputAction = InputAction.INSERT

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
                if (isInternalChange) return

                val originalValue = editable?.toString()
                val transformedValue = transformUserInput(originalValue)
                    .let { maskValue?.evaluate(it, inputAction) ?: it }

                if (originalValue != transformedValue)
                    applyInternalChange(transformedValue)

                publishChangeEvent(editable)
            }

            private fun applyInternalChange(value: String?) {
                val editable = input.editableText
                val originalFilters = editable.filters

                isInternalChange = true

                // disable filters on the underlying input applied by the input/keyboard type
                editable.filters = emptyArray()
                editable.replace(0, editable.length, value)
                editable.filters = originalFilters

                isInternalChange = false
            }

            private fun publishChangeEvent(editable: Editable?) {
                val event = ChangeEvent(
                    isComplete = maskValue?.isComplete(editable?.toString()) ?: false,
                    isEmpty = editable?.isEmpty() ?: false,
                    isValid = validate(getText())
                )

                eventListeners.change.forEach {
                    it(event)
                }
            }
        })

        input.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                eventListeners.focus.forEach { it(FocusEvent()) }
            else
                eventListeners.blur.forEach { it(BlurEvent()) }
        }
    }
}
