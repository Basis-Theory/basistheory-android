package com.basistheory.android.example

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.basistheory.android.example.view.MainActivity
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Assert.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class VirtualCardTests {

    @get:Rule
    val activityRule = activityScenarioRule<MainActivity>()

    @Before
    fun before() {
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.nav_virtual_card)).perform(click())
    }

    @Test
    fun virtualCardIsReadOnly() {
        onView(withId(R.id.readonly_card_number))
            .perform(scrollTo())
            .check(matches(allOf(isDisplayed(), not(isEnabled()))))

        onView(withId(R.id.readonly_expiration_date))
            .perform(scrollTo())
            .check(matches(allOf(isDisplayed(), not(isEnabled()))))
    }

    @Test
    fun textIsKeptInSyncBetweenFormAndVirtualCard() {
        val cardNumber = "4242 4242 4242 4242"
        val expMonth = "11"
        val expYear = (LocalDate.now().year + 1).toString()
        val expDate = "$expMonth/${expYear.takeLast(2)}"

        // type values into editable elements
        onView(withId(R.id.card_number)).perform(scrollTo(), typeText(cardNumber))
        onView(withId(R.id.expiration_date)).perform(scrollTo(), typeText(expDate))

        // assertions on readonly virtual card
        onView(allOf(
            isDescendantOfA(withId(R.id.virtual_card_container)),
            withText(cardNumber)
        )).check(matches(isDisplayed()))

        onView(allOf(
            isDescendantOfA(withId(R.id.virtual_card_container)),
            withText(expDate)
        )).check(matches(isDisplayed()))
    }
}