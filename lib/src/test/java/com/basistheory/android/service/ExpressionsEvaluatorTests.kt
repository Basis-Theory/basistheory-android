package com.basistheory.android.service

import com.basistheory.android.context.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

class ExpressionsEvaluatorTest {
    @After
    fun tearDown() {
        ExpressionsEvaluator.clear()
    }

    @Test
    fun `evaluate with string expression returns evaluated expression`() {
        val name = "John Doe"
        val expression = "{{ name }}"
        val expected = name
        val expressionsContext = mapOf("name" to name)

        setExpressionsContext(expressionsContext)

        val result = ExpressionsEvaluator.evaluate(expression)

        assertEquals(expected, result)
    }

    @Test
    fun `evaluate with multiple string expression and built-in filters`() {
        val expression = "{{ firstName | upcase }} {{ lastName | downcase }}"
        val firstName = "john"
        val lastName = "DOE"
        val expressionsContext = mapOf("firstName" to firstName, "lastName" to lastName)

        setExpressionsContext(expressionsContext)

        val result = ExpressionsEvaluator.evaluate(expression)

        assertEquals("JOHN doe", result)
    }

    @Test
    fun `evaluate with map expression returns evaluated expression`() {
        val name = "John Doe"
        val age = 25
        val expression = mapOf(
            "name" to "{{ name }}",
            "age" to age
        )
        val expected = mapOf(
            "name" to name,
            "age" to age
        )
        val expressionsContext = mapOf("name" to name)

        setExpressionsContext(expressionsContext)

        val result = ExpressionsEvaluator.evaluate(expression)

        assertEquals(expected, result)
    }

    @Test
    fun `evaluate with list expression evaluates list elements`() {
        val expression = "{{ name }}"
        val expected = "John Doe"
        val expressionsContext = mapOf("name" to expected)
        val list = listOf(expression, null, 42, true)
        val expectedList = listOf(expected, null, 42, true)

        setExpressionsContext(expressionsContext)

        val result = ExpressionsEvaluator.evaluate(list)

        assertEquals(expectedList, result)
    }
}