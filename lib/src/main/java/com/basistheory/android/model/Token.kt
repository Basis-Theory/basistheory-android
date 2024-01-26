package com.basistheory.android.model

import java.time.OffsetDateTime
import java.util.UUID

class Token(
    var id: String,

    var tenantId: UUID,

    var type: String,

    var data: Any,

    var mask: Any? = null,

    var fingerprint: String? = null,

    var fingerprintExpression: String? = null,

    var enrichments: Any? = null,

    var expiresAt: OffsetDateTime? = null,

    var createdBy: UUID,

    var createdAt: OffsetDateTime,

    var modifiedBy: UUID? = null,

    var modifiedAt: OffsetDateTime? = null,

    var metadata: Map<String, String>? = null,

    var containers: List<String>,

    var searchIndexes: List<String>? = null,
)

internal fun com.basistheory.Token.toAndroid(): Token =
    Token(
        this.id!!,
        this.tenantId!!,
        this.type!!,
        this.data!!,
        this.mask,
        this.fingerprint,
        this.fingerprintExpression,
        this.enrichments,
        this.expiresAt,
        this.createdBy!!,
        this.createdAt!!,
        this.modifiedBy,
        this.modifiedAt,
        this.metadata,
        this.containers!!,
        this.searchIndexes
    )