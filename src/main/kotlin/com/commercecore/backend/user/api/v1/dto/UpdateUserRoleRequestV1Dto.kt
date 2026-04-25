package com.commercecore.backend.user.api.v1.dto

import jakarta.validation.constraints.NotBlank

data class UpdateUserRoleRequestV1Dto(

    @field:NotBlank(message = "El rol es obligatorio")
    val role: String
)