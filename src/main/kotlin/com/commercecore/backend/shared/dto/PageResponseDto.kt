package com.commercecore.backend.shared.dto

data class PageResponseDto<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean,
    val sortBy: String,
    val sortDir: String
)