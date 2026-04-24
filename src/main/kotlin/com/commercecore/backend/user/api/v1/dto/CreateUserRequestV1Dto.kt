package com.commercecore.backend.user.api.v1.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateUserRequestV1Dto(

    @field:NotBlank(message = "El nombre es obligatorio")
    @field:Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    val name: String,

    @field:NotBlank(message = "El correo es obligatorio")
    @field:Email(message = "El correo no tiene un formato válido")
    @field:Size(max = 150, message = "El correo no puede exceder 150 caracteres")
    val email: String,

    @field:NotBlank(message = "La contraseña es obligatoria")
    @field:Size(min = 4, max = 100, message = "La contraseña debe tener entre 4 y 100 caracteres")
    val password: String
)