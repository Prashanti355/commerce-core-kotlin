package com.commercecore.backend.shared.util

import com.commercecore.backend.shared.dto.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

object ResponseUtil {

    fun <T> success(message: String, data: T): ResponseEntity<ApiResponse<T>> {
        val response = ApiResponse(
            code = HttpStatus.OK.value(),
            message = message,
            data = data
        )
        return ResponseEntity.ok(response)
    }

    fun <T> created(message: String, data: T): ResponseEntity<ApiResponse<T>> {
        val response = ApiResponse(
            code = HttpStatus.CREATED.value(),
            message = message,
            data = data
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    fun error(status: HttpStatus, message: String): ResponseEntity<ApiResponse<Nothing>> {
        val response = ApiResponse<Nothing>(
            code = status.value(),
            message = message,
            data = null
        )
        return ResponseEntity.status(status).body(response)
    }
}