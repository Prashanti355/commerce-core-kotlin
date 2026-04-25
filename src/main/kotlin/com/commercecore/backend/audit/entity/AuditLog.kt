package com.commercecore.backend.audit.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "audit_logs")
class AuditLog(

    @Id
    @Column(nullable = false, updatable = false)
    var id: UUID = UUID.randomUUID(),

    @Column(name = "actor_email", length = 150)
    var actorEmail: String? = null,

    @Column(nullable = false, length = 100)
    var action: String,

    @Column(name = "entity_type", nullable = false, length = 50)
    var entityType: String,

    @Column(name = "entity_id")
    var entityId: Long? = null,

    @Column(nullable = false, length = 20)
    var status: String,

    @Column(columnDefinition = "TEXT")
    var detail: String? = null,

    @Column(name = "ip_address", length = 100)
    var ipAddress: String? = null,

    @Column(name = "request_path", length = 255)
    var requestPath: String? = null,

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)