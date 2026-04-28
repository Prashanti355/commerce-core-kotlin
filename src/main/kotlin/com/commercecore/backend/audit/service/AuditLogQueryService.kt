package com.commercecore.backend.audit.service

import com.commercecore.backend.audit.api.v1.dto.AuditLogResponseV1Dto
import com.commercecore.backend.shared.dto.PageResponseDto
import java.time.LocalDateTime
import java.util.UUID

interface AuditLogQueryService {

    fun getAuditLogs(
        page: Int,
        size: Int,
        sortBy: String,
        sortDir: String,
        actorEmail: String?,
        action: String?,
        entityType: String?,
        status: String?,
        from: LocalDateTime?,
        to: LocalDateTime?
    ): PageResponseDto<AuditLogResponseV1Dto>

    fun getAuditLogById(id: UUID): AuditLogResponseV1Dto
}