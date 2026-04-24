package com.commercecore.backend.product.service

import com.commercecore.backend.product.api.v1.dto.CreateProductRequestV1Dto
import com.commercecore.backend.product.api.v1.dto.PatchProductRequestV1Dto
import com.commercecore.backend.product.api.v1.dto.ProductResponseV1Dto
import com.commercecore.backend.product.api.v1.dto.UpdateProductRequestV1Dto
import com.commercecore.backend.product.api.v1.mapper.ProductMapperV1
import com.commercecore.backend.product.entity.Product
import com.commercecore.backend.product.exception.InvalidPatchProductRequestException
import com.commercecore.backend.product.exception.ProductConflictException
import com.commercecore.backend.product.exception.ProductDeletedException
import com.commercecore.backend.product.exception.ProductNotDeletedException
import com.commercecore.backend.product.exception.ProductNotFoundException
import com.commercecore.backend.product.repository.ProductRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository
) : ProductService {

    override fun getAllProducts(): List<ProductResponseV1Dto> {
        return productRepository.findAllByDeletedFalse().map(ProductMapperV1::toResponseDto)
    }

    override fun getDeletedProducts(): List<ProductResponseV1Dto> {
        return productRepository.findAllByDeletedTrue().map(ProductMapperV1::toResponseDto)
    }

    override fun getProductById(id: Long): ProductResponseV1Dto {
        val product = getNonDeletedProductOrThrow(id)
        return ProductMapperV1.toResponseDto(product)
    }

    override fun searchProducts(name: String): List<ProductResponseV1Dto> {
        return productRepository.findAllByDeletedFalseAndNameContainingIgnoreCase(name)
            .map(ProductMapperV1::toResponseDto)
    }

    override fun createProduct(createProductRequestV1Dto: CreateProductRequestV1Dto): ProductResponseV1Dto {
        ensureSkuAvailable(createProductRequestV1Dto.sku)

        val product = Product(
            name = createProductRequestV1Dto.name,
            description = createProductRequestV1Dto.description,
            price = createProductRequestV1Dto.price,
            stock = createProductRequestV1Dto.stock,
            sku = createProductRequestV1Dto.sku,
            imageUrl = createProductRequestV1Dto.imageUrl
        )

        val savedProduct = productRepository.save(product)
        return ProductMapperV1.toResponseDto(savedProduct)
    }

    override fun updateProduct(id: Long, updateProductRequestV1Dto: UpdateProductRequestV1Dto): ProductResponseV1Dto {
        val product = getNonDeletedProductOrThrow(id)

        ensureSkuAvailable(updateProductRequestV1Dto.sku, id)

        product.name = updateProductRequestV1Dto.name
        product.description = updateProductRequestV1Dto.description
        product.price = updateProductRequestV1Dto.price
        product.stock = updateProductRequestV1Dto.stock
        product.sku = updateProductRequestV1Dto.sku
        product.imageUrl = updateProductRequestV1Dto.imageUrl

        val updatedProduct = productRepository.save(product)
        return ProductMapperV1.toResponseDto(updatedProduct)
    }

    override fun patchProduct(id: Long, patchProductRequestV1Dto: PatchProductRequestV1Dto): ProductResponseV1Dto {
        val product = getNonDeletedProductOrThrow(id)

        if (
            patchProductRequestV1Dto.name == null &&
            patchProductRequestV1Dto.description == null &&
            patchProductRequestV1Dto.price == null &&
            patchProductRequestV1Dto.stock == null &&
            patchProductRequestV1Dto.sku == null &&
            patchProductRequestV1Dto.imageUrl == null
        ) {
            throw InvalidPatchProductRequestException()
        }

        patchProductRequestV1Dto.name?.let { product.name = it }
        patchProductRequestV1Dto.description?.let { product.description = it }
        patchProductRequestV1Dto.price?.let { product.price = it }
        patchProductRequestV1Dto.stock?.let { product.stock = it }
        patchProductRequestV1Dto.sku?.let {
            ensureSkuAvailable(it, id)
            product.sku = it
        }
        patchProductRequestV1Dto.imageUrl?.let { product.imageUrl = it }

        val updatedProduct = productRepository.save(product)
        return ProductMapperV1.toResponseDto(updatedProduct)
    }

    override fun deactivateProduct(id: Long): ProductResponseV1Dto {
        val product = getNonDeletedProductOrThrow(id)
        product.active = false

        val updatedProduct = productRepository.save(product)
        return ProductMapperV1.toResponseDto(updatedProduct)
    }

    override fun activateProduct(id: Long): ProductResponseV1Dto {
        val product = getNonDeletedProductOrThrow(id)
        product.active = true

        val updatedProduct = productRepository.save(product)
        return ProductMapperV1.toResponseDto(updatedProduct)
    }

    override fun deleteProduct(id: Long): ProductResponseV1Dto {
        val product = getProductOrThrow(id)

        if (product.deleted) {
            throw ProductDeletedException()
        }

        product.deleted = true
        product.deletedAt = LocalDateTime.now()
        product.active = false

        val updatedProduct = productRepository.save(product)
        return ProductMapperV1.toResponseDto(updatedProduct)
    }

    override fun restoreProduct(id: Long): ProductResponseV1Dto {
        val product = getProductOrThrow(id)

        if (!product.deleted) {
            throw ProductNotDeletedException()
        }

        ensureSkuAvailable(product.sku, id)

        product.deleted = false
        product.deletedAt = null
        product.active = false

        val updatedProduct = productRepository.save(product)
        return ProductMapperV1.toResponseDto(updatedProduct)
    }

    private fun getProductOrThrow(id: Long): Product {
        return productRepository.findById(id)
            .orElseThrow { ProductNotFoundException() }
    }

    private fun getNonDeletedProductOrThrow(id: Long): Product {
        val product = getProductOrThrow(id)

        if (product.deleted) {
            throw ProductDeletedException("El producto está eliminado. Restáuralo antes de operar sobre él")
        }

        return product
    }

    private fun ensureSkuAvailable(sku: String, currentProductId: Long? = null) {
        val skuOwner = productRepository.findBySkuAndDeletedFalse(sku)

        if (skuOwner.isPresent && skuOwner.get().id != currentProductId) {
            throw ProductConflictException()
        }
    }
}