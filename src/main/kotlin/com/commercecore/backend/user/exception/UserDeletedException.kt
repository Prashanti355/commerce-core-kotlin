package com.commercecore.backend.user.exception

class UserDeletedException(message: String = "El usuario ya está eliminado") : RuntimeException(message)