package com.commercecore.backend.product.service

import com.commercecore.backend.audit.service.AuditLogService
import com.commercecore.backend.product.api.v1.dto.CreateProductRequestV1Dto
import com.commercecore.backend.product.api.v1.dto.PatchProductRequestV1Dto
import com.commercecore.backend.product.entity.Product
import com.commercecore.backend.product.exception.InvalidPatchProductRequestException
import com.commercecore.backend.product.exception.ProductConflictException
import com.commercecore.backend.product.exception.ProductNotDeletedException
import com.commercecore.backend.product.repository.ProductRepository
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class ProductServiceImplTest {

    @Mock
    private lateinit var productRepository: ProductRepository

    @Mock
    private lateinit var auditLogService: AuditLogService

    private lateinit var productService: ProductServiceImpl

    @BeforeEach
    fun setUp() {
        productService = ProductServiceImpl(productRepository, auditLogService)
    }

    @Test
    fun `debe lanzar conflicto si el sku ya existe`() {
        val request = CreateProductRequestV1Dto(
            name = "Teclado",
            description = "Mecánico",
            price = BigDecimal("999.99"),
            stock = 5,
            sku = "SKU-001",
            imageUrl = null
        )

        `when`(productRepository.findBySkuAndDeletedFalse("SKU-001"))
            .thenReturn(Optional.of(productBase()))

        assertThatThrownBy {
            productService.createProduct(request)
        }.isInstanceOf(ProductConflictException::class.java)
    }

    @Test
    fun `debe fallar patch vacio`() {
        `when`(productRepository.findById(1L))
            .thenReturn(Optional.of(productBase(id = 1L)))

        assertThatThrownBy {
            productService.patchProduct(1L, PatchProductRequestV1Dto())
        }.isInstanceOf(InvalidPatchProductRequestException::class.java)
    }

    @Test
    fun `debe fallar restore si el producto no esta eliminado`() {
        `when`(productRepository.findById(1L))
            .thenReturn(Optional.of(productBase(id = 1L, deleted = false)))

        assertThatThrownBy {
            productService.restoreProduct(1L)
        }.isInstanceOf(ProductNotDeletedException::class.java)
    }

    private fun productBase(
        id: Long = 1L,
        deleted: Boolean = false
    ): Product {
        return Product(
            id = id,
            name = "Teclado mecánico",
            description = "Switches azules",
            price = BigDecimal("1399.99"),
            stock = 10,
            sku = "TEC-MEC-001",
            imageUrl = "https://example.com/teclado.jpg",
            active = true,
            deleted = deleted
        )
    }
}