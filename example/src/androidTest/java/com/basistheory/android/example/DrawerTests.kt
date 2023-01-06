package com.basistheory.android.example

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.*
import androidx.test.espresso.contrib.DrawerMatchers.*
import androidx.test.espresso.contrib.NavigationViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.basistheory.android.example.view.MainActivity
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
class DrawerTests {

    @get:Rule
    val activityRule = activityScenarioRule<MainActivity>()

    @Test
    fun canOpenAndCloseDrawer() {
        onView(withId(R.id.drawer_layout)).check(matches(isClosed()))

        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()))

        onView(withId(R.id.drawer_layout)).perform(close())
        onView(withId(R.id.drawer_layout)).check(matches(isClosed()))
    }

    @Test
    fun canNavigateThroughDrawerItems() {
        mapOf(
            R.id.nav_card to R.string.title_card,
            R.id.nav_social_security_number to R.string.title_social_security_number,
            R.id.nav_custom_form to R.string.title_custom_form,
            R.id.nav_virtual_card to R.string.title_virtual_card
        ).forEach { (navId, stringId) ->
            // navigate to view
            onView(withId(R.id.drawer_layout)).perform(open())
            onView(withId(navId)).perform(click())

            // assert that the fragment title is displayed in the action bar
            onView(
                allOf(
                    withText(stringId),
                    withParent(withId(androidx.appcompat.R.id.action_bar))
                )
            ).check(matches(isDisplayed()))
        }
    }
}
