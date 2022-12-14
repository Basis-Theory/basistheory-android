package com.basistheory.android.view.validation

class LuhnValidator: ElementValidator() {

    override fun validate(value: String?): Boolean {
        if (value.isNullOrEmpty() || value.any { !it.isDigit() }) return false

        var sum = 0
        var isDoubled = false

        for (i in value.length - 1 downTo 0) {
            val digit: Int = value[i] - '0'
            if (digit < 0 || digit > 9) {
                // Ignore non-digits
                continue
            }
            var addend: Int
            if (isDoubled) {
                addend = digit * 2
                if (addend > 9) {
                    addend -= 9
                }
            } else {
                addend = digit
            }
            sum += addend
            isDoubled = !isDoubled
        }

        return sum % 10 == 0
    }
}
