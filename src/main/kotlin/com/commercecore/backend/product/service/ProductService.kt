package com.commercecore.backend.product.service

import com.commercecore.backend.product.api.v1.dto.CreateProductRequestV1Dto
import com.commercecore.backend.product.api.v1.dto.PatchProductRequestV1Dto
import com.commercecore.backend.product.api.v1.dto.ProductResponseV1Dto
import com.commercecore.backend.product.api.v1.dto.UpdateProductRequestV1Dto

interface ProductService {

    fun getAllProducts(): List<ProductResponseV1Dto>

    fun getDeletedProducts(): List<ProductResponseV1Dto>

    fun getProductById(id: Long): ProductResponseV1Dto

    fun searchProducts(name: String): List<ProductResponseV1Dto>

    fun createProduct(createProductRequestV1Dto: CreateProductRequestV1Dto): ProductResponseV1Dto

    fun updateProduct(id: Long, updateProductRequestV1Dto: UpdateProductRequestV1Dto): ProductResponseV1Dto

    fun patchProduct(id: Long, patchProductRequestV1Dto: PatchProductRequestV1Dto): ProductResponseV1Dto

    fun deactivateProduct(id: Long): ProductResponseV1Dto

    fun activateProduct(id: Long): ProductResponseV1Dto

    fun deleteProduct(id: Long): ProductResponseV1Dto

    fun restoreProduct(id: Long): ProductResponseV1Dto
}