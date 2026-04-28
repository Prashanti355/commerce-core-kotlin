package com.commercecore.backend.audit.api.v1.mapper

import com.commercecore.backend.audit.api.v1.dto.AuditLogResponseV1Dto
import com.commercecore.backend.audit.entity.AuditLog

object AuditLogMapperV1 {

    fun toResponseDto(auditLog: AuditLog): AuditLogResponseV1Dto {
        return AuditLogResponseV1Dto(
            id = auditLog.id,
            actorEmail = auditLog.actorEmail,
            action = auditLog.action,
            entityType = auditLog.entityType,
            entityId = auditLog.entityId,
            status = auditLog.status,
            detail = auditLog.detail,
            ipAddress = auditLog.ipAddress,
            requestPath = auditLog.requestPath,
            createdAt = auditLog.createdAt
        )
    }
}