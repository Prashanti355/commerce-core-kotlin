package com.commercecore.backend.product.exception

class InvalidPatchProductRequestException(
    message: String = "Debes enviar al menos un campo para actualizar"
) : RuntimeException(message)