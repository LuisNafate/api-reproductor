package com.example.data.repositories

import com.example.domain.models.Album
import java.time.LocalDate
import java.util.UUID

/**
 * Interfaz del repositorio de Álbumes
 */
interface AlbumRepository {
    suspend fun create(title: String, artistId: UUID, releaseDate: LocalDate?, coverImageUrl: String?, genre: String?): Album
    suspend fun findById(id: UUID): Album?
    suspend fun findAll(limit: Int = 100, offset: Int = 0): List<Album>
    suspend fun findByArtistId(artistId: UUID): List<Album>
    suspend fun update(id: UUID, title: String?, artistId: UUID?, releaseDate: LocalDate?, coverImageUrl: String?, genre: String?): Boolean
    suspend fun delete(id: UUID): Boolean
}
