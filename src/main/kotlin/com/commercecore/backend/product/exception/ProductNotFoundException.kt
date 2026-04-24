package com.commercecore.backend.product.exception

class ProductNotFoundException(
    message: String = "Producto no encontrado"
) : RuntimeException(message)