package com.commercecore.backend.auth.service

import java.time.LocalDateTime

interface TokenBlacklistService {

    fun isBlacklisted(jti: String): Boolean

    fun blacklistToken(
        jti: String,
        userId: Long,
        expiresAt: LocalDateTime,
        reason: String? = null
    )
}