package com.basistheory.android.context

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

class ExpressionsContextTest {
    @After
    fun tearDown() {
        clearExpressionsContext()
    }

    @Test
    fun `setContext adds values to the expressions context`() {
        val contextValues = mapOf(
            "name" to "John Doe",
            "age" to 25
        )

        setExpressionsContext(contextValues)

        val expressionsContext = getExpressionsContext()

        assertEquals(contextValues, expressionsContext)
    }

    @Test
    fun `registerValue assigns a unique key and adds value to the expressions context`() {
        val value = "John Doe"

        val key = registerExpressionValue(value)

        val expressionsContext = getExpressionsContext()

        assertEquals(value, expressionsContext[key])
    }

    @Test
    fun `getValues returns the current expressions context`() {
        val contextValues = mapOf(
            "name" to "John Doe",
            "age" to 25
        )

        setExpressionsContext(contextValues)

        val expressionsContext = getExpressionsContext()

        assertEquals(contextValues, expressionsContext)
    }

    @Test
    fun `clear removes all values from the expressions context`() {
        val contextValues = mapOf(
            "name" to "John Doe",
            "age" to 25
        )

        setExpressionsContext(contextValues)

        clearExpressionsContext()

        val expressionsContext = getExpressionsContext()

        assertEquals(emptyMap<String, Any?>(), expressionsContext)
    }
}