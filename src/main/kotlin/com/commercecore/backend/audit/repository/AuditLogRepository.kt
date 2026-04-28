package com.commercecore.backend.audit.repository

import com.commercecore.backend.audit.entity.AuditLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.UUID

interface AuditLogRepository :
    JpaRepository<AuditLog, UUID>,
    JpaSpecificationExecutor<AuditLog>