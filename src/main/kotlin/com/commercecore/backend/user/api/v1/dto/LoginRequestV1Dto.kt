package com.commercecore.backend.auth.api.v1.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequestV1Dto(

    @field:NotBlank(message = "El correo es obligatorio")
    @field:Email(message = "El correo no tiene un formato válido")
    val email: String,

    @field:NotBlank(message = "La contraseña es obligatoria")
    val password: String
)