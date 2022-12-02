package com.basistheory.android.view.transform

fun regexReplaceElementTransform(
    regex: Regex,
    replacement: String = ""
): (value: String?) -> String? = {
    if (it == null) null else regex.replace(it, replacement)
}