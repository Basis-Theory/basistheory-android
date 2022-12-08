package com.basistheory.android.example

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.basistheory.android.example.view.MainActivity
import org.hamcrest.Matchers.allOf
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class CollectCardTests {

    @get:Rule
    val activityRule = activityScenarioRule<MainActivity>()

    @Before
    fun before() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.nav_card)).perform(click())
    }

    @Test
    fun canSetText() {
        onView(withId(R.id.`@+id/autofill_button`)).perform(scrollTo(), click())

        onView(withText("4242 4242 4242 4242")).check(matches(isDisplayed()))
        onView(withText("12/25")).check(matches(isDisplayed()))
        onView(withText("123")).check(matches(isDisplayed()))
    }

    @Test
    fun canTokenize() {
        val cardNumber = "4242424242424242"
        val expMonth = "11"
        val expYear = (LocalDate.now().year + 1).toString()
        val cvc = "123"

        // type values into elements
        onView(withId(R.id.cardNumber)).perform(scrollTo(), typeText(cardNumber))
        onView(withId(R.id.cardExpiration)).perform(scrollTo(), typeText("$expMonth/${expYear.takeLast(2)}"))
        onView(withId(R.id.cvc)).perform(scrollTo(), typeText(cvc))

        // click tokenize
        onView(withId(R.id.tokenize_button)).perform(scrollTo(), click())

        // assertions on tokenize response
        onView(withId(R.id.tokenize_result)).check(
            matches(
                allOf(
                    withSubstring(cardNumber), // displayed with mask, but transformed back to this value
                    withSubstring(expMonth),
                    withSubstring(expYear),
                    withSubstring(cvc)
                )
            )
        )
    }
}