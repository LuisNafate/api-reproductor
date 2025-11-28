package com.example.data.repositories

import com.example.domain.models.Artist
import java.util.UUID

/**
 * Interfaz del repositorio de Artistas
 * Define las operaciones de acceso a datos para Artist
 */
interface ArtistRepository {
    suspend fun create(name: String, biography: String?, country: String?, imageUrl: String?): Artist
    suspend fun findById(id: UUID): Artist?
    suspend fun findAll(limit: Int = 100, offset: Int = 0): List<Artist>
    suspend fun update(id: UUID, name: String?, biography: String?, country: String?, imageUrl: String?): Boolean
    suspend fun delete(id: UUID): Boolean
    suspend fun searchByName(name: String): List<Artist>
}
