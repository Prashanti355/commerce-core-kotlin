package com.commercecore.backend.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.jwt")
data class JwtProperties(
    var secret: String = "",
    var accessTokenExpirationMs: Long = 900_000,
    var refreshTokenExpirationMs: Long = 604_800_000
)