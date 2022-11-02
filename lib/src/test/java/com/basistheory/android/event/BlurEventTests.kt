package com.basistheory.android.event

import android.app.Activity
import android.widget.FrameLayout
import com.basistheory.android.view.TextElement
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isEmpty
import strikt.assertions.isFalse
import strikt.assertions.isTrue


@RunWith(RobolectricTestRunner::class)
class BlurEventTests {
    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity

    private lateinit var textElement: TextElement

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()

        textElement = TextElement(activity)

        // adds something else that can be blured, o/w textElement always auto regains blur
        FrameLayout(activity).apply {
            isFocusable = true
            addView(textElement)
        }
    }

    @Test
    fun `BlurEvent is raised when element loses focus`() {
        textElement.requestFocus()

        val blurEvents = mutableListOf<BlurEvent>()

        textElement.addBlurEventListener { blurEvents.add(it) }
        textElement.clearFocus()

        expectThat(textElement.hasFocus()).isFalse()
        expectThat(blurEvents).hasSize(1)
    }

    @Test
    fun `BlurEvent is NOT raised when element is not focused and focus is cleared`() {
        val blurEvents = mutableListOf<BlurEvent>()

        textElement.addBlurEventListener { blurEvents.add(it) }
        textElement.clearFocus()

        expectThat(textElement.hasFocus()).isFalse()
        expectThat(blurEvents).isEmpty()
    }

    @Test
    fun `BlurEvent is NOT raised when element is focused`() {
        val blurEvents = mutableListOf<BlurEvent>()

        textElement.addBlurEventListener { blurEvents.add(it) }
        textElement.requestFocus()

        expectThat(textElement.hasFocus()).isTrue()
        expectThat(blurEvents).isEmpty()
    }

    @Test
    fun `BlurEvent is NOT raised when element is programmatically changed`() {
        val blurEvents = mutableListOf<BlurEvent>()

        textElement.addBlurEventListener { blurEvents.add(it) }
        textElement.setText("foo")

        expectThat(textElement.hasFocus()).isFalse()
        expectThat(blurEvents).isEmpty()
    }
}