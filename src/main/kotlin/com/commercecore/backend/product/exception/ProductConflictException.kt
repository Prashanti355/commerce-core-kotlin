package com.commercecore.backend.product.exception

class ProductConflictException(
    message: String = "Ya existe un producto activo con ese SKU"
) : RuntimeException(message)