package com.commercecore.backend.auth.service

import com.commercecore.backend.auth.entity.TokenBlacklist
import com.commercecore.backend.auth.repository.TokenBlacklistRepository
import com.commercecore.backend.user.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TokenBlacklistServiceImpl(
    private val tokenBlacklistRepository: TokenBlacklistRepository,
    private val userRepository: UserRepository
) : TokenBlacklistService {

    override fun isBlacklisted(jti: String): Boolean {
        return tokenBlacklistRepository.existsByJti(jti)
    }

    override fun blacklistToken(
        jti: String,
        userId: Long,
        expiresAt: LocalDateTime,
        reason: String?
    ) {
        if (tokenBlacklistRepository.existsByJti(jti)) {
            return
        }

        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("Usuario no encontrado para blacklist") }

        val blacklistedToken = TokenBlacklist(
            jti = jti,
            user = user,
            expiresAt = expiresAt,
            reason = reason
        )

        tokenBlacklistRepository.save(blacklistedToken)
    }
}