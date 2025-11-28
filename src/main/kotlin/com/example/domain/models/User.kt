package com.example.domain.models

import java.time.LocalDateTime
import java.util.UUID

/**
 * Modelo de dominio para Usuario
 */
data class User(
    val id: UUID,
    val username: String,
    val email: String,
    val role: String, // USER, ADMIN
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
