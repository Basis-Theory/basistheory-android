package com.basistheory.android.example

import android.content.ClipboardManager
import android.content.Context
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.basistheory.android.example.util.clickOnRightDrawable
import com.basistheory.android.example.util.waitUntilVisible
import com.basistheory.android.example.util.withDrawableRight
import com.basistheory.android.example.view.MainActivity
import net.datafaker.Faker
import org.hamcrest.Matchers.allOf
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CollectCustomFormTests {

    @get:Rule
    val activityRule = activityScenarioRule<MainActivity>()

    private lateinit var appContext: Context
    private lateinit var clipboardManager: ClipboardManager

    @Before
    fun before() {
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.nav_custom_form)).perform(click())

        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        clipboardManager = appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }



    @Test
    fun canAutofill() {
        onView(withId(R.id.autofill_button)).perform(scrollTo(), click())

        onView(withText("John Doe")).check(matches(isDisplayed()))
        onView(withText("+1(234) 567-8900")).check(matches(isDisplayed()))
        onView(withText("ABC-123")).check(matches(isDisplayed()))
        onView(withText("secret password 123")).check(matches(isDisplayed()))
        onView(withText("1234")).check(matches(isDisplayed()))
    }

    @Test
    fun canTokenize() {
        val name = Faker().name().fullName()
        val phoneNumber = "2345678900"
        val orderNumber = "ABC123"
        val password = Faker().zelda().character()
        val pin = Faker().number().randomNumber(4, true).toString()

        // type values into elements
        onView(withId(R.id.name)).perform(scrollTo(), typeText(name))
        onView(withId(R.id.phoneNumber)).perform(scrollTo(), typeText(phoneNumber))
        onView(withId(R.id.orderNumber)).perform(scrollTo(), typeText(orderNumber))
        onView(withId(R.id.password)).perform(scrollTo(), typeText(password))
        onView(withId(R.id.pin)).perform(scrollTo(), typeText(pin))

        // click tokenize
        onView(withId(R.id.tokenize_button)).perform(scrollTo(), click())

        // assertions on tokenize response
        onView(withId(R.id.result))
            .perform(waitUntilVisible())
            .check(
                matches(
                    allOf(
                        withSubstring(name),
                        withSubstring("+1(234) 567-8900"),
                        withSubstring("ABC-123"),
                        withSubstring(password),
                        withSubstring(pin)
                    )
                )
            )
    }

    @Test
    fun canCopy() {
        val textToCopy = Faker().name().firstName()
        onView(withId(R.id.name)).perform(waitUntilVisible())
        onView(withId(R.id.name)).perform(scrollTo(), typeText(textToCopy))

        // check icon exists
        onView(withId(R.id.name)).check(matches(withDrawableRight()))

        // click on it
        onView(withId(R.id.name)).perform(clickOnRightDrawable())

        // check value got added to clipboard
        val clipData = clipboardManager.primaryClip
        val clipboardText = clipData?.getItemAt(0)?.text.toString()

        assert(clipboardText == textToCopy)
    }
}