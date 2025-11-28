package com.example.domain.models

import java.time.LocalDateTime
import java.util.UUID

/**
 * Modelo de dominio para un Artista
 */
data class Artist(
    val id: UUID,
    val name: String,
    val biography: String?,
    val country: String?,
    val imageUrl: String?,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
