package com.commercecore.backend.auth.service

import com.commercecore.backend.audit.service.AuditLogService
import com.commercecore.backend.auth.api.v1.dto.AuthMeResponseV1Dto
import com.commercecore.backend.auth.api.v1.dto.AuthTokensResponseV1Dto
import com.commercecore.backend.auth.api.v1.dto.LoginRequestV1Dto
import com.commercecore.backend.auth.api.v1.dto.RefreshTokenRequestV1Dto
import com.commercecore.backend.auth.security.CustomUserDetails
import com.commercecore.backend.auth.security.UserDetailsServiceImpl
import com.commercecore.backend.user.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import java.time.ZoneOffset

@Service
class AuthServiceImpl(
    private val authenticationManager: AuthenticationManager,
    private val userDetailsServiceImpl: UserDetailsServiceImpl,
    private val jwtService: JwtService,
    private val tokenBlacklistService: TokenBlacklistService,
    private val userRepository: UserRepository,
    private val auditLogService: AuditLogService
) : AuthService {

    override fun login(loginRequestV1Dto: LoginRequestV1Dto): AuthTokensResponseV1Dto {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                loginRequestV1Dto.email,
                loginRequestV1Dto.password
            )
        )

        val userDetails = userDetailsServiceImpl
            .loadUserByUsername(loginRequestV1Dto.email) as CustomUserDetails

        auditLogService.log(
            action = "LOGIN",
            entityType = "AUTH",
            entityId = userDetails.id,
            detail = "Inicio de sesión exitoso",
            actorEmail = userDetails.email
        )

        return AuthTokensResponseV1Dto(
            accessToken = jwtService.generateAccessToken(userDetails),
            refreshToken = jwtService.generateRefreshToken(userDetails)
        )
    }

    override fun refresh(refreshTokenRequestV1Dto: RefreshTokenRequestV1Dto): AuthTokensResponseV1Dto {
        val refreshToken = refreshTokenRequestV1Dto.refreshToken
        val username = jwtService.extractUsername(refreshToken)
        val jti = jwtService.extractJti(refreshToken)

        if (tokenBlacklistService.isBlacklisted(jti)) {
            throw IllegalArgumentException("El refresh token está revocado")
        }

        val userDetails = userDetailsServiceImpl.loadUserByUsername(username) as CustomUserDetails

        if (!jwtService.isTokenValid(refreshToken, userDetails, "refresh")) {
            throw IllegalArgumentException("Refresh token inválido")
        }

        tokenBlacklistService.blacklistToken(
            jti = jti,
            userId = userDetails.id,
            expiresAt = jwtService.extractExpiration(refreshToken)
                .toInstant()
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime(),
            reason = "refresh"
        )

        auditLogService.log(
            action = "TOKEN_REFRESH",
            entityType = "AUTH",
            entityId = userDetails.id,
            detail = "Refresh token utilizado correctamente",
            actorEmail = userDetails.email
        )

        return AuthTokensResponseV1Dto(
            accessToken = jwtService.generateAccessToken(userDetails),
            refreshToken = jwtService.generateRefreshToken(userDetails)
        )
    }

    override fun logout(bearerToken: String) {
        val token = bearerToken.removePrefix("Bearer ").trim()
        val claims = jwtService.extractAllClaims(token)
        val userId = (claims["userId"] as Number).toLong()
        val username = claims.subject

        tokenBlacklistService.blacklistToken(
            jti = claims.id,
            userId = userId,
            expiresAt = claims.expiration.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime(),
            reason = "logout"
        )

        auditLogService.log(
            action = "LOGOUT",
            entityType = "AUTH",
            entityId = userId,
            detail = "Sesión cerrada correctamente",
            actorEmail = username
        )
    }

    override fun me(email: String): AuthMeResponseV1Dto {
        val user = userRepository.findByEmailAndDeletedFalse(email)
            .orElseThrow { IllegalArgumentException("Usuario no encontrado") }

        return AuthMeResponseV1Dto(
            id = user.id ?: throw IllegalStateException("Usuario sin id"),
            email = user.email,
            role = user.role
        )
    }
}