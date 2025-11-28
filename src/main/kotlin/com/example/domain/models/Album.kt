package com.example.domain.models

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

/**
 * Modelo de dominio para un Álbum
 */
data class Album(
    val id: UUID,
    val title: String,
    val artistId: UUID,
    val releaseDate: LocalDate?,
    val coverImageUrl: String?,
    val genre: String?,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
