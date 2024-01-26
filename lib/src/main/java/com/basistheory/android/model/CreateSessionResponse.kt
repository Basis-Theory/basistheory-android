package com.basistheory.android.model

import java.time.OffsetDateTime

class CreateSessionResponse(
    var sessionKey: String,

    var nonce: String,

    var expiresAt: OffsetDateTime
)

internal fun com.basistheory.CreateSessionResponse.toAndroid(): CreateSessionResponse =
    CreateSessionResponse(
        this.sessionKey!!,
        this.nonce!!,
        this.expiresAt!!
    )