package com.commercecore.backend.product.repository

import com.commercecore.backend.product.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface ProductRepository : JpaRepository<Product, Long> {

    fun findAllByDeletedFalse(): List<Product>

    fun findAllByDeletedTrue(): List<Product>

    fun findByIdAndDeletedFalse(id: Long): Optional<Product>

    fun findBySkuAndDeletedFalse(sku: String): Optional<Product>

    fun existsBySkuAndDeletedFalse(sku: String): Boolean

    fun findAllByDeletedFalseAndNameContainingIgnoreCase(name: String): List<Product>
}