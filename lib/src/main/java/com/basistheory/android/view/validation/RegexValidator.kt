package com.basistheory.android.view.validation

class RegexValidator(
    private val regex: Regex
): ElementValidator() {

    constructor(pattern: String): this(Regex(pattern))

    override fun validate(value: String?): Boolean {
        return regex.matches(value ?: "")
    }
}
