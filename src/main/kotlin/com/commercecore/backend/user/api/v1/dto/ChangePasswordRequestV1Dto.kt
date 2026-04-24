package com.commercecore.backend.user.api.v1.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ChangePasswordRequestV1Dto(

    @field:NotBlank(message = "La contraseña actual es obligatoria")
    val currentPassword: String,

    @field:NotBlank(message = "La nueva contraseña es obligatoria")
    @field:Size(min = 4, max = 100, message = "La nueva contraseña debe tener entre 4 y 100 caracteres")
    val newPassword: String
)