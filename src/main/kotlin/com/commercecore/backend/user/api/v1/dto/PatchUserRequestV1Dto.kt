package com.commercecore.backend.user.api.v1.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

data class PatchUserRequestV1Dto(

    @field:Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    val name: String? = null,

    @field:Email(message = "El correo no tiene un formato válido")
    @field:Size(max = 150, message = "El correo no puede exceder 150 caracteres")
    val email: String? = null
)