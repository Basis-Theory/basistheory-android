package com.basistheory.android.event

import android.app.Activity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.basistheory.android.view.TextElement
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import strikt.api.expectThat
import strikt.assertions.*


@RunWith(RobolectricTestRunner::class)
class FocusEventTests {
    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity

    private lateinit var textElement: TextElement

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()

        textElement = TextElement(activity)

        // adds something else that can be focused, o/w textElement always auto regains focus
        FrameLayout(activity).apply {
            isFocusable = true
            addView(textElement)
        }
    }

    @Test
    fun `FocusEvent is raised when element is focused`() {
        val focusEvents = mutableListOf<FocusEvent>()

        textElement.addFocusEventListener { focusEvents.add(it) }
        textElement.requestFocus()

        expectThat(textElement.hasFocus()).isTrue()
        expectThat(focusEvents).hasSize(1)
    }

    @Test
    fun `FocusEvent is NOT raised when element has focus and is focused again`() {
        textElement.requestFocus() // give the element initial focus

        val focusEvents = mutableListOf<FocusEvent>()

        textElement.addFocusEventListener { focusEvents.add(it) }
        textElement.requestFocus()

        expectThat(textElement.hasFocus()).isTrue()
        expectThat(focusEvents).isEmpty()
    }

    @Test
    fun `FocusEvent is NOT raised when element loses focus`() {
        textElement.requestFocus() // give the element initial focus

        val focusEvents = mutableListOf<FocusEvent>()

        textElement.addFocusEventListener { focusEvents.add(it) }
        textElement.clearFocus()

        expectThat(textElement.hasFocus()).isFalse()
        expectThat(focusEvents).isEmpty()
    }

    @Test
    fun `FocusEvent is NOT raised when element is programmatically changed`() {
        val focusEvents = mutableListOf<FocusEvent>()

        textElement.addFocusEventListener { focusEvents.add(it) }
        textElement.setText("foo")

        expectThat(textElement.hasFocus()).isFalse()
        expectThat(focusEvents).isEmpty()
    }
}