package com.commercecore.backend.user.exception

class UserConflictException(message: String = "Ya existe un usuario con ese correo") : RuntimeException(message)