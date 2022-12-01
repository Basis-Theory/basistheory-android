package com.basistheory.android.event

import android.app.Activity
import com.basistheory.android.view.TextElement
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isFalse
import strikt.assertions.isTrue


@RunWith(RobolectricTestRunner::class)
class ChangeEventTests {
    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity

    private lateinit var textElement: TextElement

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()

        textElement = TextElement(activity)
    }

    @Test
    fun `ChangeEvent is raised when text is changed`() {
        val changeEvents = mutableListOf<ChangeEvent>()

        textElement.addChangeEventListener { changeEvents.add(it) }

        textElement.setText("f")
        textElement.setText("fo")
        textElement.setText("foo")

        expectThat(changeEvents).hasSize(3)
    }

    @Test
    fun `ChangeEvent is raised with expected content`() {
        val changeEvents = mutableListOf<ChangeEvent>()

        textElement.addChangeEventListener { changeEvents.add(it) }

        textElement.setText("f")
        textElement.setText("")
        textElement.setText(null)

        expectThat(changeEvents).hasSize(3).and {
            get { elementAt(0).isEmpty }.isFalse()
            get { elementAt(1).isEmpty }.isTrue()
            get { elementAt(2).isEmpty }.isTrue()
        }
    }

    @Test
    fun `ChangeEvent is raised when setText applies the same text value`() {
        val textValue = "foo"
        val changeEvents = mutableListOf<ChangeEvent>()

        textElement.addChangeEventListener { changeEvents.add(it) }
        textElement.setText(textValue)
        textElement.setText(textValue)

        expectThat(changeEvents).hasSize(2)
    }
}