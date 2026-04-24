package com.commercecore.backend.auth.repository

import com.commercecore.backend.auth.entity.TokenBlacklist
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface TokenBlacklistRepository : JpaRepository<TokenBlacklist, UUID> {

    fun findByJti(jti: String): Optional<TokenBlacklist>

    fun existsByJti(jti: String): Boolean
}