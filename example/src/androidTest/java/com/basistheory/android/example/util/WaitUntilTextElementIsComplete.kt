package com.basistheory.android.example.util

import android.view.View
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.util.HumanReadables
import com.basistheory.android.view.TextElement
import org.hamcrest.Matcher
import org.hamcrest.Matchers.any
import java.util.concurrent.TimeoutException

/**
 * @return a [WaitUntilTextElementIsComplete] instance created with the given [timeout] parameter.
 */
fun waitUntilTextElementIsComplete(timeout: Long = 10000L): ViewAction {
    return WaitUntilTextElementIsComplete(timeout)
}

/**
 * A [ViewAction] that waits up to [timeout] milliseconds for a [TextElement] to be complete.
 */
private class WaitUntilTextElementIsComplete(private val timeout: Long) : ViewAction {

    override fun getConstraints(): Matcher<View> {
        return any(View::class.java)
    }

    override fun getDescription(): String {
        return "wait up to $timeout milliseconds for the Element to be complete"
    }

    override fun perform(uiController: UiController, view: View) {

        val endTime = System.currentTimeMillis() + timeout

        do {
            if ((view as TextElement).isComplete) return
            uiController.loopMainThreadForAtLeast(50)
        } while (System.currentTimeMillis() < endTime)

        throw PerformException.Builder()
            .withActionDescription(description)
            .withCause(TimeoutException("Waited $timeout milliseconds"))
            .withViewDescription(HumanReadables.describe(view))
            .build()
    }
}