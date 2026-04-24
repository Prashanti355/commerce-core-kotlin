package com.commercecore.backend.product.api.v1.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class ProductResponseV1Dto(
    val id: Long?,
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val stock: Int,
    val sku: String,
    val imageUrl: String?,
    val active: Boolean,
    val deleted: Boolean,
    val deletedAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)