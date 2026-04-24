package com.commercecore.backend.user.exception

class InvalidCurrentPasswordException(
    message: String = "La contraseña actual no es correcta"
) : RuntimeException(message)