package com.commercecore.backend.audit.api.v1.controller

import com.commercecore.backend.audit.service.AuditLogQueryService
import com.commercecore.backend.shared.util.ResponseUtil
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.UUID

@RestController
@RequestMapping("/api/v1/audit-logs")
@SecurityRequirement(name = "bearerAuth")
class AuditLogControllerV1(
    private val auditLogQueryService: AuditLogQueryService
) {

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    fun getAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "createdAt") sortBy: String,
        @RequestParam(defaultValue = "desc") sortDir: String,
        @RequestParam(required = false) actorEmail: String?,
        @RequestParam(required = false) action: String?,
        @RequestParam(required = false) entityType: String?,
        @RequestParam(required = false) status: String?,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        from: LocalDateTime?,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        to: LocalDateTime?
    ) = ResponseUtil.success(
        message = "Lista de registros de auditoría",
        data = auditLogQueryService.getAuditLogs(
            page = page,
            size = size,
            sortBy = sortBy,
            sortDir = sortDir,
            actorEmail = actorEmail,
            action = action,
            entityType = entityType,
            status = status,
            from = from,
            to = to
        )
    )

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID) = ResponseUtil.success(
        message = "Registro de auditoría encontrado",
        data = auditLogQueryService.getAuditLogById(id)
    )
}