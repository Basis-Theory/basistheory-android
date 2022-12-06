package com.basistheory.android.view.validation

import org.threeten.bp.LocalDate

fun futureDateValidator(value: String?, currentDate: LocalDate = LocalDate.now()): Boolean {
    try {
        if (value.isNullOrEmpty()) return false

        val segments = value.split("/")
        if (segments.count() != 2) return false

        val month = segments[0].toInt()
        val year = 2000 + segments[1].toInt()

        return currentDate.withDayOfMonth(1) <= LocalDate.of(year, month, 1)
    } catch (t: Throwable) {
        return false
    }
}