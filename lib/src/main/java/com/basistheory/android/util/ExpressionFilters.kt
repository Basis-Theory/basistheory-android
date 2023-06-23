package com.basistheory.android.util

import liqp.TemplateContext
import liqp.filters.Filter


internal fun registerExpressionFilters() {
    Filter.registerFilter(object : Filter("pad_left") {
        override fun apply(value: Any, context: TemplateContext, vararg params: Any): String {
            val length = params[0].toString().toInt()
            val placeholder = params[1].toString().toCharArray()[0]

            return super.asString(value, context).padStart(length, placeholder)
        }
    })

    Filter.registerFilter(object : Filter("pad_right") {
        override fun apply(value: Any, context: TemplateContext, vararg params: Any): String {
            val length = params[0].toString().toInt()
            val placeholder = params[1].toString().toCharArray()[0]

            return super.asString(value, context).padEnd(length, placeholder)
        }
    })
}