package com.commercecore.backend.product.api.v1.controller

import com.commercecore.backend.product.api.v1.dto.CreateProductRequestV1Dto
import com.commercecore.backend.product.api.v1.dto.PatchProductRequestV1Dto
import com.commercecore.backend.product.api.v1.dto.UpdateProductRequestV1Dto
import com.commercecore.backend.product.service.ProductService
import com.commercecore.backend.shared.util.ResponseUtil
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/products")
class ProductControllerV1(
    private val productService: ProductService
) {

    @GetMapping
    fun getAll() = ResponseUtil.success(
        message = "Lista de productos",
        data = productService.getAllProducts()
    )

    @GetMapping("/deleted")
    @SecurityRequirement(name = "bearerAuth")
    fun getDeleted() = ResponseUtil.success(
        message = "Lista de productos eliminados",
        data = productService.getDeletedProducts()
    )

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long) = ResponseUtil.success(
        message = "Producto encontrado",
        data = productService.getProductById(id)
    )

    @GetMapping("/search")
    fun search(@RequestParam name: String) = ResponseUtil.success(
        message = "Resultados de búsqueda",
        data = productService.searchProducts(name)
    )

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    fun create(@Valid @RequestBody createProductRequestV1Dto: CreateProductRequestV1Dto) =
        ResponseUtil.created(
            message = "Producto creado",
            data = productService.createProduct(createProductRequestV1Dto)
        )

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody updateProductRequestV1Dto: UpdateProductRequestV1Dto
    ) = ResponseUtil.success(
        message = "Producto actualizado",
        data = productService.updateProduct(id, updateProductRequestV1Dto)
    )

    @PatchMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    fun patch(
        @PathVariable id: Long,
        @Valid @RequestBody patchProductRequestV1Dto: PatchProductRequestV1Dto
    ) = ResponseUtil.success(
        message = "Producto actualizado parcialmente",
        data = productService.patchProduct(id, patchProductRequestV1Dto)
    )

    @PatchMapping("/{id}/deactivate")
    @SecurityRequirement(name = "bearerAuth")
    fun deactivate(@PathVariable id: Long) = ResponseUtil.success(
        message = "Producto desactivado",
        data = productService.deactivateProduct(id)
    )

    @PatchMapping("/{id}/activate")
    @SecurityRequirement(name = "bearerAuth")
    fun activate(@PathVariable id: Long) = ResponseUtil.success(
        message = "Producto activado",
        data = productService.activateProduct(id)
    )

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    fun delete(@PathVariable id: Long) = ResponseUtil.success(
        message = "Producto eliminado lógicamente",
        data = productService.deleteProduct(id)
    )

    @PatchMapping("/{id}/restore")
    @SecurityRequirement(name = "bearerAuth")
    fun restore(@PathVariable id: Long) = ResponseUtil.success(
        message = "Producto restaurado",
        data = productService.restoreProduct(id)
    )
}