package com.commercecore.backend.product.exception

class ProductNotDeletedException(
    message: String = "El producto no está eliminado"
) : RuntimeException(message)