package com.example.domain.models

import java.time.LocalDateTime
import java.util.UUID

/**
 * Modelo de dominio para una Canción
 */
data class Song(
    val id: UUID,
    val title: String,
    val artistId: UUID,
    val albumId: UUID?,
    val durationSeconds: Int,
    val fileUrl: String,
    val genre: String?,
    val playCount: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
