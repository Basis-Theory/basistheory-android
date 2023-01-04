package com.basistheory.android.view

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.os.bundleOf
import com.basistheory.android.R

open class ReadOnlyTextElement @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val textView = AppCompatTextView(context, null, android.R.attr.textViewStyle)
    private var defaultBackground = textView.background

    init {
        textView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        super.addView(textView)

        // wires up attributes declared in the xml layout with properties on this element
        context.theme.obtainStyledAttributes(attrs, R.styleable.ReadOnlyTextElement, defStyleAttr, 0)
            .apply {
                try {
                    textColor = getColor(R.styleable.ReadOnlyTextElement_textColor, Color.BLACK)
                    removeDefaultStyles =
                        getBoolean(R.styleable.ReadOnlyTextElement_removeDefaultStyles, false)
                    setText(getString(R.styleable.ReadOnlyTextElement_text))
                } finally {
                    recycle()
                }
            }
    }

    var elementRef: TextElement? = null
        set(value) {
            if (value != null && elementRef !== value) {
                field = value
                field?.addChangeEventListener { setText(field?.getText()) }
            } else {
                field = value
            }
        }

    fun setText(value: String?) {
        textView.text = value
    }

    var textColor: Int
        get() = textView.currentTextColor
        set(value) = textView.setTextColor(value)

    var removeDefaultStyles: Boolean
        get() = textView.background == null
        set(value) {
            textView.background = if (value) null else defaultBackground
        }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection? {
        return textView.onCreateInputConnection(outAttrs)
    }

    override fun onSaveInstanceState(): Parcelable {
        return bundleOf(
            STATE_SUPER to super.onSaveInstanceState(),
            STATE_TEXT_VIEW to textView.onSaveInstanceState()
        )
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            textView.onRestoreInstanceState(state.getParcelable(STATE_TEXT_VIEW))
            super.onRestoreInstanceState(state.getParcelable(STATE_SUPER))
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    internal companion object {
        private const val STATE_SUPER = "state_super"
        private const val STATE_TEXT_VIEW = "state_input"
    }
}
