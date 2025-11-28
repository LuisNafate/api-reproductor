package com.example.presentation.dto

/**
 * DTOs genéricos para respuestas de la API
 */
data class ErrorResponse(
    val error: String,
    val message: String,
    val status: Int
)

data class SuccessResponse(
    val message: String,
    val data: Any? = null
)

data class PaginatedResponse<T>(
    val data: List<T>,
    val page: Int,
    val pageSize: Int,
    val total: Int
)
