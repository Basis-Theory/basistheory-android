package com.basistheory.android.model

import com.google.gson.annotations.SerializedName
import java.time.OffsetDateTime
import java.util.UUID

class Token(
    @SerializedName("id") var id: String,

    @SerializedName("tenant_id") var tenantId: UUID,

    @SerializedName("type") var type: String,

    @SerializedName("data") var data: Any,

    @SerializedName("mask") var mask: Any? = null,

    @SerializedName("fingerprint") var fingerprint: String? = null,

    @SerializedName("fingerprint_expression") var fingerprintExpression: String? = null,

    @SerializedName("enrichments") var enrichments: Any? = null,

    @SerializedName("expires_at") var expiresAt: OffsetDateTime? = null,

    @SerializedName("created_by") var createdBy: UUID,

    @SerializedName("created_at") var createdAt: OffsetDateTime,

    @SerializedName("modified_by") var modifiedBy: UUID? = null,

    @SerializedName("modified_at") var modifiedAt: OffsetDateTime? = null,

    @SerializedName("metadata") var metadata: Map<String, String>? = null,

    @SerializedName("containers") var containers: List<String>,

    @SerializedName("search_indexes") var searchIndexes: List<String>? = null,
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