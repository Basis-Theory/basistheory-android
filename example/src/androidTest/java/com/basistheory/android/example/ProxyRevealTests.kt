package com.basistheory.android.example

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.basistheory.android.example.util.waitUntilTextElementIsComplete
import com.basistheory.android.example.view.MainActivity
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProxyRevealTests {

    @get:Rule
    val activityRule = activityScenarioRule<MainActivity>()

    @Before
    fun before() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.nav_reveal)).perform(click())
    }

    @Test
    fun canProxyAndReveal() {
        // click reveal
        onView(withId(R.id.reveal_button)).perform(scrollTo(), click())

        // assertions on read only card number
        onView(withId(R.id.revealedData))
            .perform(waitUntilTextElementIsComplete())
    }

    @Test
    fun cardNumberIsReadOnly() {
        onView(withId(R.id.revealedData))
            .perform(scrollTo())
            .check(matches(allOf(isDisplayed(), not(isEnabled()))))
    }
}
