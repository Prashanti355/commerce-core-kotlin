package com.commercecore.backend.product.exception

class ProductDeletedException(
    message: String = "El producto está eliminado"
) : RuntimeException(message)