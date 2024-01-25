package com.basistheory.android.model

import com.google.gson.annotations.SerializedName
import java.time.OffsetDateTime

class CreateSessionResponse(
    @SerializedName("session_key") var sessionKey: String,

    @SerializedName("nonce") var nonce: String,

    @SerializedName("expires_at") var expiresAt: OffsetDateTime
)

internal fun com.basistheory.CreateSessionResponse.toAndroid(): CreateSessionResponse =
    CreateSessionResponse(
        this.sessionKey!!,
        this.nonce!!,
        this.expiresAt!!
    )