package com.example.domain.models

import java.time.LocalDateTime
import java.util.UUID

/**
 * Modelo de dominio para una Playlist
 */
data class Playlist(
    val id: UUID,
    val name: String,
    val description: String?,
    val userId: UUID,
    val isPublic: Boolean = true,
    val coverImageUrl: String?,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

/**
 * Relación muchos a muchos entre Playlist y Song
 */
data class PlaylistSong(
    val playlistId: UUID,
    val songId: UUID,
    val position: Int,
    val addedAt: LocalDateTime = LocalDateTime.now()
)
