package com.commercecore.backend.user.exception

class InvalidPatchRequestException(
    message: String = "Debes enviar al menos un campo para actualizar"
) : RuntimeException(message)