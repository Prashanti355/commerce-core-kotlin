package com.commercecore.backend.audit.repository.specification

import com.commercecore.backend.audit.entity.AuditLog
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDateTime

object AuditLogSpecifications {

    fun filters(
        actorEmail: String?,
        action: String?,
        entityType: String?,
        status: String?,
        from: LocalDateTime?,
        to: LocalDateTime?
    ): Specification<AuditLog> {
        return Specification { root, _, cb ->
            val predicates = mutableListOf<jakarta.persistence.criteria.Predicate>()

            if (!actorEmail.isNullOrBlank()) {
                predicates += cb.like(
                    cb.lower(root.get("actorEmail")),
                    "%${actorEmail.trim().lowercase()}%"
                )
            }

            if (!action.isNullOrBlank()) {
                predicates += cb.equal(
                    cb.upper(root.get("action")),
                    action.trim().uppercase()
                )
            }

            if (!entityType.isNullOrBlank()) {
                predicates += cb.equal(
                    cb.upper(root.get("entityType")),
                    entityType.trim().uppercase()
                )
            }

            if (!status.isNullOrBlank()) {
                predicates += cb.equal(
                    cb.upper(root.get("status")),
                    status.trim().uppercase()
                )
            }

            from?.let {
                predicates += cb.greaterThanOrEqualTo(root.get("createdAt"), it)
            }

            to?.let {
                predicates += cb.lessThanOrEqualTo(root.get("createdAt"), it)
            }

            cb.and(*predicates.toTypedArray())
        }
    }
}