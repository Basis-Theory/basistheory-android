package com.basistheory.android.example

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.basistheory.android.example.util.waitUntilTextElementIsComplete
import com.basistheory.android.example.util.waitUntilVisible
import com.basistheory.android.example.view.MainActivity
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

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
        val cardNumber = "4242424242424242"
        val expMonth = "11"
        val expYear = (LocalDate.now().year + 1).toString()
        val cvc = "123"

        // type values into elements
        onView(withId(R.id.card_number)).perform(scrollTo(), typeText(cardNumber))
        onView(withId(R.id.expiration_date)).perform(
            scrollTo(),
            typeText("$expMonth/${expYear.takeLast(2)}")
        )
        onView(withId(R.id.cvc)).perform(scrollTo(), typeText(cvc))

        // click tokenize
        onView(withId(R.id.tokenize_button)).perform(closeSoftKeyboard(), click())

        // wait for tokenize result
        onView(withId(R.id.tokenize_result))
            .perform(waitUntilVisible())

        // click reveal
        onView(withId(R.id.reveal_button)).perform(click())

        // assertions on read only elements
        onView(withId(R.id.revealedCardNumber))
            .perform(scrollTo(), waitUntilTextElementIsComplete())

//        onView(withId(R.id.revealedExpirationDate))
//            .perform(waitUntilTextElementIsComplete())
//
//        onView(withId(R.id.revealedCvc))
//            .perform(waitUntilTextElementIsComplete())
    }

    @Test
    fun revealedElementsAreReadOnly() {
        onView(withId(R.id.revealedCardNumber))
            .perform(scrollTo())
            .check(matches(allOf(isDisplayed(), not(isEnabled()))))

        onView(withId(R.id.revealedExpirationDate))
            .perform(scrollTo())
            .check(matches(allOf(isDisplayed(), not(isEnabled()))))

        onView(withId(R.id.revealedCvc))
            .perform(scrollTo())
            .check(matches(allOf(isDisplayed(), not(isEnabled()))))
    }
}
