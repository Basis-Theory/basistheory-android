package com.basistheory.android.example.util

import android.view.View
import androidx.annotation.DrawableRes
import com.basistheory.android.view.TextElement
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

fun withDrawableRight() = object : TypeSafeMatcher<View>() {
    override fun describeTo(description: Description) {
        description.appendText("view has a right compound drawable")
    }

    override fun matchesSafely(view: View?): Boolean {
        if (view == null) {
            return false
        }

        val drawables = (view as TextElement).getDrawables()
        drawables[2] ?: return false

        return true
    }
}