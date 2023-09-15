package com.basistheory.android.example.util

import android.view.MotionEvent
import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import com.basistheory.android.view.TextElement
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

fun clickOnRightDrawable(): ViewAction {
    return ClickRightDrawableAction()
}

private class ClickRightDrawableAction() : ViewAction {
    override fun getConstraints(): Matcher<View> {
        return allOf(ViewMatchers.isDisplayed(), withDrawableRight())
    }

    override fun getDescription(): String {
        return "Click on Drawable"
    }

    override fun perform(uiController: UiController, view: View) {
        if (view.requestFocusFromTouch()) {
            try {
                val drawables = (view as TextElement).getDrawables()

                if (drawables[2] == null) {
                    throw RuntimeException("No right drawable found for clicking.")
                }

                val drawableBounds = drawables[2]?.bounds

                val x = view.right - (drawableBounds?.width()?.div(2) ?: 0)
                val y = view.y + (drawableBounds?.height()?.div(2) ?: 0)

                if (view.dispatchTouchEvent(
                        MotionEvent.obtain(
                            android.os.SystemClock.uptimeMillis(),
                            android.os.SystemClock.uptimeMillis(),
                            MotionEvent.ACTION_DOWN,
                            x.toFloat(),
                            y,
                            0
                        )
                    )
                ) {
                    view.dispatchTouchEvent(
                        MotionEvent.obtain(
                            android.os.SystemClock.uptimeMillis(),
                            android.os.SystemClock.uptimeMillis(),
                            MotionEvent.ACTION_UP,
                            x.toFloat(),
                            y,
                            0
                        )
                    )
                }
            } catch (e: Exception) {
                throw RuntimeException("Error performing click on Drawable: ${e.message}")
            }
        }
    }
}


