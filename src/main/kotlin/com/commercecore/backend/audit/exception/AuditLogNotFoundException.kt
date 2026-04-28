package com.commercecore.backend.audit.exception

class AuditLogNotFoundException(
    message: String = "Registro de auditoría no encontrado"
) : RuntimeException(message)