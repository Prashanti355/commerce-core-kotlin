package com.commercecore.backend.audit.api.v1.dto

import java.time.LocalDateTime
import java.util.UUID

data class AuditLogResponseV1Dto(
    val id: UUID,
    val actorEmail: String?,
    val action: String,
    val entityType: String,
    val entityId: Long?,
    val status: String,
    val detail: String?,
    val ipAddress: String?,
    val requestPath: String?,
    val createdAt: LocalDateTime
)