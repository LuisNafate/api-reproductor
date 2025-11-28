package com.example.data.repositories

import com.example.domain.models.Song
import java.util.UUID

/**
 * Interfaz del repositorio de Canciones
 */
interface SongRepository {
    suspend fun create(title: String, artistId: UUID, albumId: UUID?, durationSeconds: Int, fileUrl: String, genre: String?): Song
    suspend fun findById(id: UUID): Song?
    suspend fun findAll(limit: Int = 100, offset: Int = 0): List<Song>
    suspend fun findByArtistId(artistId: UUID): List<Song>
    suspend fun findByAlbumId(albumId: UUID): List<Song>
    suspend fun update(id: UUID, title: String?, artistId: UUID?, albumId: UUID?, durationSeconds: Int?, fileUrl: String?, genre: String?): Boolean
    suspend fun delete(id: UUID): Boolean
    suspend fun incrementPlayCount(id: UUID): Boolean
    suspend fun searchByTitle(title: String): List<Song>
}
