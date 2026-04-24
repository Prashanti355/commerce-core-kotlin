package com.commercecore.backend.user.api.v1.dto

import java.time.LocalDateTime

data class UserResponseV1Dto(
    val id: Long?,
    val name: String,
    val email: String,
    val active: Boolean,
    val deleted: Boolean,
    val deletedAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)