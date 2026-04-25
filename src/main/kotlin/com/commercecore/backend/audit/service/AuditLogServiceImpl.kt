package com.commercecore.backend.audit.service

import com.commercecore.backend.audit.entity.AuditLog
import com.commercecore.backend.audit.repository.AuditLogRepository
import com.commercecore.backend.audit.service.AuditLogService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Service
class AuditLogServiceImpl(
    private val auditLogRepository: AuditLogRepository
) : AuditLogService {

    override fun log(
        action: String,
        entityType: String,
        entityId: Long?,
        status: String,
        detail: String?,
        actorEmail: String?
    ) {
        val request = currentRequest()
        val resolvedActor = actorEmail ?: currentActor()

        val auditLog = AuditLog(
            actorEmail = resolvedActor,
            action = action,
            entityType = entityType,
            entityId = entityId,
            status = status,
            detail = detail,
            ipAddress = request?.remoteAddr,
            requestPath = request?.requestURI
        )

        auditLogRepository.save(auditLog)
    }

    private fun currentActor(): String? {
        val authentication = SecurityContextHolder.getContext().authentication
        val name = authentication?.name

        return if (name.isNullOrBlank() || name == "anonymousUser") null else name
    }

    private fun currentRequest(): HttpServletRequest? {
        val attributes = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
        return attributes?.request
    }
}