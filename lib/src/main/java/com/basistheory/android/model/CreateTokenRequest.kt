package com.basistheory.android.model

import com.google.gson.annotations.SerializedName

class CreateTokenRequest(
    @SerializedName("id") var id: String? = null,

    @SerializedName("type") var type: String,

    @SerializedName("data") var data: Any,

    @SerializedName("mask") var mask: Any? = null,

    @SerializedName("fingerprint_expression") var fingerprintExpression: String? = null,

    @SerializedName("deduplicate_token") var deduplicateToken: Boolean? = null,

    @SerializedName("expires_at") var expiresAt: String? = null,

    @SerializedName("metadata") var metadata: Map<String, String>? = null,

    @SerializedName("containers") var containers: List<String>? = null,

    @SerializedName("search_indexes") var searchIndexes: List<String>? = null,
)

internal fun CreateTokenRequest.toJava(): com.basistheory.CreateTokenRequest =
    com.basistheory.CreateTokenRequest().apply {
        this.id = this@toJava.id
        this.data = this@toJava.data
        this.type = this@toJava.type
        this.mask = this@toJava.mask
        this.fingerprintExpression = this@toJava.fingerprintExpression
        this.deduplicateToken = this@toJava.deduplicateToken
        this.expiresAt = this@toJava.expiresAt
        this.metadata = this@toJava.metadata
        this.containers = this@toJava.containers
        this.searchIndexes = this@toJava.searchIndexes
    }
