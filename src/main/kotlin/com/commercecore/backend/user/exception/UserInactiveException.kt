package com.commercecore.backend.user.exception

class UserInactiveException(message: String = "El usuario ya está inactivo") : RuntimeException(message)