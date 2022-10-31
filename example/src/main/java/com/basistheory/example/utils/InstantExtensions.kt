package com.basistheory.example.utils

import java.time.Instant
import java.time.format.DateTimeFormatter

fun Instant.toIsoString(): String =
    DateTimeFormatter.ISO_INSTANT.format(this)
