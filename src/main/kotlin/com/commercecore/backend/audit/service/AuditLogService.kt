package com.commercecore.backend.audit.service

interface AuditLogService {

    fun log(
        action: String,
        entityType: String,
        entityId: Long? = null,
        status: String = "SUCCESS",
        detail: String? = null,
        actorEmail: String? = null
    )
}