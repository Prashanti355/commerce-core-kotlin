package com.commercecore.backend.auth.service

import com.commercecore.backend.auth.security.CustomUserDetails
import com.commercecore.backend.config.JwtProperties
import com.commercecore.backend.user.entity.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class JwtServiceTest {

    private val jwtService = JwtService(
        JwtProperties(
            secret = "8f1b5d3c7a9e2f4b6c8d0e1f3a5b7c9d2e4f6a8b0c1d3e5f7a9b2c4d6e8f0a1",
            accessTokenExpirationMs = 900_000,
            refreshTokenExpirationMs = 604_800_000
        )
    )

    @Test
    fun `debe generar y validar access token`() {
        val user = User(
            id = 1L,
            name = "May",
            email = "may@example.com",
            password = "hash",
            role = "ROLE_USER"
        )
        val userDetails = CustomUserDetails(user)

        val token = jwtService.generateAccessToken(userDetails)

        assertThat(jwtService.extractUsername(token)).isEqualTo("may@example.com")
        assertThat(jwtService.extractType(token)).isEqualTo("access")
        assertThat(jwtService.isTokenValid(token, userDetails, "access")).isTrue()
    }

    @Test
    fun `debe generar refresh token con tipo refresh`() {
        val user = User(
            id = 2L,
            name = "Admin",
            email = "admin@example.com",
            password = "hash",
            role = "ROLE_ADMIN"
        )
        val userDetails = CustomUserDetails(user)

        val token = jwtService.generateRefreshToken(userDetails)

        assertThat(jwtService.extractUsername(token)).isEqualTo("admin@example.com")
        assertThat(jwtService.extractType(token)).isEqualTo("refresh")
    }
}