package com.basistheory.android.event

import android.app.Activity
import com.basistheory.android.view.TextElement
import com.basistheory.android.view.mask.ElementMask
import com.basistheory.android.view.validation.RegexValidator
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isFalse
import strikt.assertions.isTrue
import strikt.assertions.single


@Config(sdk = [33]) // TODO remove once Roboelectric releases a new version supporting SDK 34 https://github.com/robolectric/robolectric/issues/8404
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

    @Test
    fun `ChangeEvent sets isMaskSatisfied to true when mask is undefined`() {
        val changeEvents = mutableListOf<ChangeEvent>()

        textElement.addChangeEventListener { changeEvents.add(it) }
        textElement.setText("1")

        expectThat(changeEvents).single()
            .get { isMaskSatisfied }.isTrue()
    }

    @Test
    fun `ChangeEvent computes isMaskSatisfied based on mask`() {
        val changeEvents = mutableListOf<ChangeEvent>()

        textElement.mask = ElementMask(listOf(Regex("""\d"""), Regex("""\d""")))
        textElement.addChangeEventListener { changeEvents.add(it) }
        textElement.setText("1")
        textElement.setText("12")

        expectThat(changeEvents).hasSize(2).and {
            get { elementAt(0).isMaskSatisfied }.isFalse()
            get { elementAt(1).isMaskSatisfied }.isTrue()
        }
    }

    @Test
    fun `ChangeEvent sets isValid to true when validator is undefined`() {
        val changeEvents = mutableListOf<ChangeEvent>()

        textElement.addChangeEventListener { changeEvents.add(it) }
        textElement.setText("1")

        expectThat(changeEvents).single()
            .get { isValid }.isTrue()
    }

    @Test
    fun `ChangeEvent computes isValid based on validator`() {
        val changeEvents = mutableListOf<ChangeEvent>()

        textElement.validator = RegexValidator("""\d{2}""")
        textElement.addChangeEventListener { changeEvents.add(it) }
        textElement.setText("1")
        textElement.setText("12")

        expectThat(changeEvents).hasSize(2).and {
            get { elementAt(0).isValid }.isFalse()
            get { elementAt(1).isValid }.isTrue()
        }
    }

    @Test
    fun `ChangeEvent computes isComplete`() {
        val changeEvents = mutableListOf<ChangeEvent>()

        textElement.mask = ElementMask(listOf(Regex("""\d"""), Regex("""\d""")))
        textElement.validator = RegexValidator("""\d{2}""")

        textElement.addChangeEventListener { changeEvents.add(it) }
        textElement.setText("1")
        textElement.setText("12")

        expectThat(changeEvents).hasSize(2).and {
            get { elementAt(0).isComplete }.isFalse()
            get { elementAt(1).isComplete }.isTrue()
        }
    }
}