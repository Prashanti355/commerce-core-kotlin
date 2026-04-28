package com.commercecore.backend.audit.service

import com.commercecore.backend.audit.api.v1.dto.AuditLogResponseV1Dto
import com.commercecore.backend.audit.api.v1.mapper.AuditLogMapperV1
import com.commercecore.backend.audit.entity.AuditLog
import com.commercecore.backend.audit.exception.AuditLogNotFoundException
import com.commercecore.backend.audit.repository.AuditLogRepository
import com.commercecore.backend.audit.repository.specification.AuditLogSpecifications
import com.commercecore.backend.shared.dto.PageResponseDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class AuditLogQueryServiceImpl(
    private val auditLogRepository: AuditLogRepository
) : AuditLogQueryService {

    override fun getAuditLogs(
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
    ): PageResponseDto<AuditLogResponseV1Dto> {
        val resolvedSortBy = resolveSortBy(sortBy)
        val resolvedSortDir = resolveSortDir(sortDir)
        val pageable = buildPageable(page, size, resolvedSortBy, resolvedSortDir)

        val auditLogPage = auditLogRepository.findAll(
            AuditLogSpecifications.filters(
                actorEmail = actorEmail,
                action = action,
                entityType = entityType,
                status = status,
                from = from,
                to = to
            ),
            pageable
        )

        return toPageResponse(auditLogPage, resolvedSortBy, resolvedSortDir)
    }

    override fun getAuditLogById(id: UUID): AuditLogResponseV1Dto {
        val auditLog = auditLogRepository.findById(id)
            .orElseThrow { AuditLogNotFoundException() }

        return AuditLogMapperV1.toResponseDto(auditLog)
    }

    private fun buildPageable(
        page: Int,
        size: Int,
        sortBy: String,
        sortDir: Sort.Direction
    ): PageRequest {
        val sanitizedPage = if (page < 0) 0 else page
        val sanitizedSize = when {
            size < 1 -> 10
            size > 100 -> 100
            else -> size
        }

        return PageRequest.of(
            sanitizedPage,
            sanitizedSize,
            Sort.by(sortDir, sortBy)
        )
    }

    private fun resolveSortBy(sortBy: String): String {
        return when (sortBy) {
            "createdAt", "actorEmail", "action", "entityType", "status" -> sortBy
            else -> "createdAt"
        }
    }

    private fun resolveSortDir(sortDir: String): Sort.Direction {
        return if (sortDir.equals("asc", ignoreCase = true)) {
            Sort.Direction.ASC
        } else {
            Sort.Direction.DESC
        }
    }

    private fun toPageResponse(
        auditLogPage: Page<AuditLog>,
        sortBy: String,
        sortDir: Sort.Direction
    ): PageResponseDto<AuditLogResponseV1Dto> {
        return PageResponseDto(
            content = auditLogPage.content.map(AuditLogMapperV1::toResponseDto),
            page = auditLogPage.number,
            size = auditLogPage.size,
            totalElements = auditLogPage.totalElements,
            totalPages = auditLogPage.totalPages,
            last = auditLogPage.isLast,
            sortBy = sortBy,
            sortDir = sortDir.name.lowercase()
        )
    }
}