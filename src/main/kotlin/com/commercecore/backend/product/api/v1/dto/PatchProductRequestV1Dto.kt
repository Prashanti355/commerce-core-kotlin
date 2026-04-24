package com.commercecore.backend.product.api.v1.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class PatchProductRequestV1Dto(

    @field:Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    val name: String? = null,

    @field:Size(max = 2000, message = "La descripción no puede exceder 2000 caracteres")
    val description: String? = null,

    @field:DecimalMin(value = "0.00", inclusive = true, message = "El precio no puede ser negativo")
    val price: BigDecimal? = null,

    @field:Min(value = 0, message = "El stock no puede ser negativo")
    val stock: Int? = null,

    @field:Size(max = 50, message = "El SKU no puede exceder 50 caracteres")
    val sku: String? = null,

    @field:Size(max = 500, message = "La URL de imagen no puede exceder 500 caracteres")
    val imageUrl: String? = null
)