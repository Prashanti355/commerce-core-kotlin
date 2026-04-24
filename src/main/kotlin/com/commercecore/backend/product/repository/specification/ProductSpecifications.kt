package com.commercecore.backend.product.repository.specification

import com.commercecore.backend.product.entity.Product
import org.springframework.data.jpa.domain.Specification
import java.math.BigDecimal

object ProductSpecifications {

    fun publicFilters(
        name: String?,
        active: Boolean?,
        minPrice: BigDecimal?,
        maxPrice: BigDecimal?
    ): Specification<Product> {
        return Specification { root, _, cb ->
            val predicates = mutableListOf<jakarta.persistence.criteria.Predicate>()

            predicates += cb.isFalse(root.get<Boolean>("deleted"))

            if (!name.isNullOrBlank()) {
                predicates += cb.like(
                    cb.lower(root.get("name")),
                    "%${name.trim().lowercase()}%"
                )
            }

            active?.let {
                predicates += if (it) {
                    cb.isTrue(root.get<Boolean>("active"))
                } else {
                    cb.isFalse(root.get<Boolean>("active"))
                }
            }

            minPrice?.let {
                predicates += cb.greaterThanOrEqualTo(root.get("price"), it)
            }

            maxPrice?.let {
                predicates += cb.lessThanOrEqualTo(root.get("price"), it)
            }

            cb.and(*predicates.toTypedArray())
        }
    }

    fun deletedFilters(
        name: String?,
        active: Boolean?,
        minPrice: BigDecimal?,
        maxPrice: BigDecimal?
    ): Specification<Product> {
        return Specification { root, _, cb ->
            val predicates = mutableListOf<jakarta.persistence.criteria.Predicate>()

            predicates += cb.isTrue(root.get<Boolean>("deleted"))

            if (!name.isNullOrBlank()) {
                predicates += cb.like(
                    cb.lower(root.get("name")),
                    "%${name.trim().lowercase()}%"
                )
            }

            active?.let {
                predicates += if (it) {
                    cb.isTrue(root.get<Boolean>("active"))
                } else {
                    cb.isFalse(root.get<Boolean>("active"))
                }
            }

            minPrice?.let {
                predicates += cb.greaterThanOrEqualTo(root.get("price"), it)
            }

            maxPrice?.let {
                predicates += cb.lessThanOrEqualTo(root.get("price"), it)
            }

            cb.and(*predicates.toTypedArray())
        }
    }
}