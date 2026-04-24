package com.commercecore.backend.auth.service

import com.commercecore.backend.auth.security.CustomUserDetails
import com.commercecore.backend.config.JwtProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey

@Service
class JwtService(
    private val jwtProperties: JwtProperties
) {

    fun generateAccessToken(userDetails: CustomUserDetails): String {
        val now = Instant.now()
        val expiration = now.plusMillis(jwtProperties.accessTokenExpirationMs)
        val jti = UUID.randomUUID().toString()

        return Jwts.builder()
            .subject(userDetails.email)
            .id(jti)
            .claim("userId", userDetails.id)
            .claim("role", userDetails.role)
            .claim("type", "access")
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiration))
            .signWith(getSigningKey())
            .compact()
    }

    fun generateRefreshToken(userDetails: CustomUserDetails): String {
        val now = Instant.now()
        val expiration = now.plusMillis(jwtProperties.refreshTokenExpirationMs)
        val jti = UUID.randomUUID().toString()

        return Jwts.builder()
            .subject(userDetails.email)
            .id(jti)
            .claim("userId", userDetails.id)
            .claim("type", "refresh")
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiration))
            .signWith(getSigningKey())
            .compact()
    }

    fun extractAllClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .payload
    }

    fun extractUsername(token: String): String = extractAllClaims(token).subject

    fun extractJti(token: String): String =
        extractAllClaims(token).id ?: throw IllegalStateException("El token no contiene jti")

    fun extractExpiration(token: String): Date = extractAllClaims(token).expiration

    fun extractType(token: String): String =
        extractAllClaims(token)["type"]?.toString()
            ?: throw IllegalStateException("El token no contiene tipo")

    fun isTokenValid(token: String, userDetails: CustomUserDetails, expectedType: String): Boolean {
        val claims = extractAllClaims(token)
        val usernameMatches = claims.subject == userDetails.username
        val notExpired = claims.expiration.after(Date())
        val correctType = claims["type"]?.toString() == expectedType

        return usernameMatches && notExpired && correctType
    }

    private fun getSigningKey(): SecretKey {
        return Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray(StandardCharsets.UTF_8))
    }
}