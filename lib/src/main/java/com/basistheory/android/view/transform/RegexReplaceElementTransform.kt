package com.basistheory.android.view.transform

data class RegexReplaceElementTransform(
    val regex: Regex,
    val replacement: String = ""
) : ElementTransform() {
    override fun apply(value: String?): String? {
        if (value == null) return null

        return regex.replace(value, replacement)
    }
}