package com.commercecore.backend.user.exception

class InvalidRoleException(
    message: String = "Rol inválido. Roles permitidos: ROLE_USER, ROLE_ADMIN"
) : RuntimeException(message)