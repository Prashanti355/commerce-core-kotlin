package com.commercecore.backend.auth.api.v1.dto

data class AuthTokensResponseV1Dto(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer"
)