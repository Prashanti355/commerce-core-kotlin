package com.commercecore.backend.auth.api.v1.dto

data class AuthMeResponseV1Dto(
    val id: Long,
    val email: String,
    val role: String
)