package com.basistheory.android.model

class CreateTokenRequest(
    var id: String? = null,

    var type: String,

    var data: Any,

    var mask: Any? = null,

    var fingerprintExpression: String? = null,

    var deduplicateToken: Boolean? = null,

    var expiresAt: String? = null,

    var metadata: Map<String, String>? = null,

    var containers: List<String>? = null,

    var searchIndexes: List<String>? = null,
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
