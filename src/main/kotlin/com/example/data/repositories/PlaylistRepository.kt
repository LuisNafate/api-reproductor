package com.example.data.repositories

import com.example.domain.models.Playlist
import com.example.domain.models.Song
import java.util.UUID

/**
 * Interfaz del repositorio de Playlists
 */
interface PlaylistRepository {
    suspend fun create(name: String, description: String?, userId: UUID, isPublic: Boolean, coverImageUrl: String?): Playlist
    suspend fun findById(id: UUID): Playlist?
    suspend fun findAll(limit: Int = 100, offset: Int = 0): List<Playlist>
    suspend fun findByUserId(userId: UUID): List<Playlist>
    suspend fun update(id: UUID, name: String?, description: String?, isPublic: Boolean?, coverImageUrl: String?): Boolean
    suspend fun delete(id: UUID): Boolean
    suspend fun addSong(playlistId: UUID, songId: UUID, position: Int): Boolean
    suspend fun removeSong(playlistId: UUID, songId: UUID): Boolean
    suspend fun getSongs(playlistId: UUID): List<Song>
}
