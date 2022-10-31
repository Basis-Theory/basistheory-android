package com.basistheory.example

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.javafaker.Faker
import org.hamcrest.Matchers.allOf
import org.junit.Assert.*
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) // TODO: WAT??
class TextElementTests {

    @get:Rule
    val activityRule = activityScenarioRule<MainActivity>()

    @Test
    fun canSetText() {
        onView(withId(R.id.setTextButton)).perform(click())
        onView(withText("Manually Set Name")).check(matches(isDisplayed()))
        onView(withText("Manually Set Phone")).check(matches(isDisplayed()))
    }

    @Test
    fun canTokenize() {
        val name = Faker().name().fullName()
        val phoneNumber = Faker().phoneNumber().phoneNumber()

        onView(withId(R.id.name)).perform(typeText(name))
        onView(withId(R.id.phoneNumber)).perform(typeText(phoneNumber))
        onView(withText(name)).check(matches(isDisplayed()))
        onView(withId(R.id.tokenizeButton)).perform(click())
        onView(allOf(withId(R.id.tokenizeResult), withSubstring(name), withSubstring(phoneNumber)))
            .check(matches(isDisplayed()))
    }
}