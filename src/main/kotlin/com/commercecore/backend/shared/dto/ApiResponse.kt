package com.commercecore.backend.shared.dto

data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T? = null
)