package com.basistheory.android.view.transform

class RegexReplaceElementTransform(
    private val regex: Regex,
    private val replacement: String = ""
): ElementTransform() {

    override fun apply(value: String?): String? =
        if (value == null) null else regex.replace(value, replacement)
}
