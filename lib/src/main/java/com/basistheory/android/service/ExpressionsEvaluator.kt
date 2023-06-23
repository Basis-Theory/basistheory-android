package com.basistheory.android.service

import com.basistheory.android.context.*
import liqp.Template

class ExpressionsEvaluator {
    companion object {
        private val expressionsContext = getExpressionsContext()

        fun clear() = clearExpressionsContext()

        fun evaluate(value: Any): Any =
            when (value) {
                is String -> evaluateString(value)
                is Map<*, *> -> evaluateMap(value)
                is List<*> -> evaluateList(value)
                else -> value
            }

        private fun evaluateExpressions(
            expression: String,
        ) = run {
            Template.parse(expression).render(expressionsContext)
        }

        private fun evaluateString(expression: String): String = evaluateExpressions(expression)

        private fun evaluateMap(value: Map<*, *>): Any = value.mapValues { (_, v) ->
            evaluate(v!!)
        }

        private fun evaluateList(value: List<*>): List<*> =
            value.map {
                if (it == null) null
                else evaluate(it)
            }
    }
}

