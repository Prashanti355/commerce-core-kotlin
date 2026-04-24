package com.commercecore.backend.auth.api.v1.dto

import jakarta.validation.constraints.NotBlank

data class RefreshTokenRequestV1Dto(

    @field:NotBlank(message = "El refresh token es obligatorio")
    val refreshToken: String
)