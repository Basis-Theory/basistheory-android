package com.basistheory.android.context

import java.util.UUID

internal class ExpressionsContext {
    companion object {
        private var map = mutableMapOf<String, Any?>()

        fun setContext(value: Map<String, Any?>) = map.apply { putAll(value) }

        fun registerValue(value: Any?): String =
            UUID.randomUUID().toString().also { map[it] = value }

        fun getValues() = map

        fun clear() = map.clear()

    }
}

internal fun setExpressionsContext(expressionsContext: Map<String, Any>) =
    ExpressionsContext.setContext(expressionsContext)

internal fun registerExpressionValue(value: String?): String =
    ExpressionsContext.registerValue(value)

internal fun getExpressionsContext(): MutableMap<String, Any?> = ExpressionsContext.getValues()

internal fun clearExpressionsContext() = ExpressionsContext.clear()

