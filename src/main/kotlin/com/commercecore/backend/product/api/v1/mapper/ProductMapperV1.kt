package com.commercecore.backend.product.api.v1.mapper

import com.commercecore.backend.product.api.v1.dto.ProductResponseV1Dto
import com.commercecore.backend.product.entity.Product

object ProductMapperV1 {

    fun toResponseDto(product: Product): ProductResponseV1Dto {
        return ProductResponseV1Dto(
            id = product.id,
            name = product.name,
            description = product.description,
            price = product.price,
            stock = product.stock,
            sku = product.sku,
            imageUrl = product.imageUrl,
            active = product.active,
            deleted = product.deleted,
            deletedAt = product.deletedAt,
            createdAt = product.createdAt,
            updatedAt = product.updatedAt
        )
    }
}