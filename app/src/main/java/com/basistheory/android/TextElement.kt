package com.basistheory.android

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Editable
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatEditText

class TextElement : FrameLayout {
    private var attrs: AttributeSet? = null
    private var defStyleAttr: Int = androidx.appcompat.R.attr.editTextStyle
    private var input: AppCompatEditText = AppCompatEditText(context, attrs, defStyleAttr)

    constructor(context: Context) : super(context) {
        setStyles()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.attrs = attrs

        setStyles()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.attrs = attrs
        this.defStyleAttr = defStyleAttr

        setStyles()
    }

    init {
        input.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        super.addView(input)

//        input.addTextChangedListener(object: TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//
//            override fun afterTextChanged(p0: Editable?) {
//                // todo: publish change event
//            }
//        })
    }

    private fun setStyles() {
        context.theme.obtainStyledAttributes(attrs, R.styleable.TextElement, defStyleAttr, 0).apply {
            try {
                input.setTextColor(getColor(R.styleable.TextElement_textColor, Color.GREEN))
            } finally {
                recycle()
            }
        }
    }

    fun getTextColors(): ColorStateList = input.textColors

    fun setTextColor(color: Int) = input.setTextColor(color)

    fun setTextColor(colors: ColorStateList) = input.setTextColor(colors)

    internal fun getValue(): Editable? = input.text
}
