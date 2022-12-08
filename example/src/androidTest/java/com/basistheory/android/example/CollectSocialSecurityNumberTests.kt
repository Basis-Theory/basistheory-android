package com.basistheory.android.example

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.basistheory.android.example.view.MainActivity
import com.github.javafaker.Faker
import org.hamcrest.Matchers.allOf
import org.junit.Assert.*
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class CollectSocialSecurityNumberTests {

    @get:Rule
    val activityRule = activityScenarioRule<MainActivity>()

    @Before
    fun before() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.nav_social_security_number)).perform(click())
    }

    @Test
    fun canSetText() {
        onView(withId(R.id.setTextButton)).perform(scrollTo(), click())

        onView(withText("234-56-7890")).check(matches(isDisplayed()))
    }

    @Test
    fun canTokenize() {
        val ssn = "123456789"

        // type values into elements
        onView(withId(R.id.socialSecurityNumber)).perform(scrollTo(), typeText(ssn))

        // click tokenize
        onView(withId(R.id.tokenizeButton)).perform(scrollTo(), click())

        // assertions on tokenize response
        onView(withId(R.id.tokenizeResult)).check(matches(withSubstring("123-45-6789")))
    }
}