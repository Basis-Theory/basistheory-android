package com.basistheory.android.example

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
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
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class TextElementTests {

    @get:Rule
    val activityRule = activityScenarioRule<MainActivity>()

    @Test
    fun canSetText() {
        onView(withId(R.id.setTextButton)).perform(click())
        onView(withText("4242 4242 4242 4242")).check(matches(isDisplayed()))
        onView(withText("Manually Set Name")).check(matches(isDisplayed()))
        onView(withText("+1(234) 567-8900")).check(matches(isDisplayed()))
        onView(withText("234-56-7890")).check(matches(isDisplayed()))
        onView(withText("ABC-123")).check(matches(isDisplayed()))
    }

    @Test
    fun canTokenize() {
        val cardNumber = "4242424242424242"
        val name = Faker().name().fullName()
        val phoneNumber = "2345678900"
        val ssn = "123456789"
        val orderNumber = "ABC123"

        // type values into elements
        onView(withId(R.id.cardNumber)).perform(typeText(cardNumber))
        onView(withId(R.id.name)).perform(typeText(name))
        onView(withId(R.id.phoneNumber)).perform(typeText(phoneNumber))
        onView(withId(R.id.socialSecurityNumber)).perform(typeText(ssn))
        onView(withId(R.id.orderNumber)).perform(typeText(orderNumber))

        // click tokenize
        onView(withId(R.id.tokenizeButton)).perform(scrollTo(), click())

        // assertions on tokenize response
        onView(withId(R.id.tokenizeResult)).check(
            matches(
                allOf(
                    withSubstring(cardNumber), // displayed with mask, but transformed back to this value
                    withSubstring(name),
                    withSubstring("+1(234) 567-8900"),
                    withSubstring("123-45-6789"),
                    withSubstring("ABC-123")
                )
            )
        )
    }
}