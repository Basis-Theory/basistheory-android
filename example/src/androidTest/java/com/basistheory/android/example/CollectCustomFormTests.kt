package com.basistheory.android.example

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.basistheory.android.example.util.waitUntilVisible
import com.basistheory.android.example.view.MainActivity
import com.github.javafaker.Faker
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

    @Before
    fun before() {
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.nav_custom_form)).perform(click())
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
}