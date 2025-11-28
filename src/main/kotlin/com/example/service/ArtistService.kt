package com.example.service

import com.example.data.repositories.ArtistRepository
import com.example.domain.models.Artist
import com.example.presentation.dto.ArtistRequest
import com.example.presentation.dto.ArtistResponse
import com.example.presentation.dto.ArtistUpdateRequest
import java.util.UUID

/**
 * Servicio para la lógica de negocio de Artistas
 */
class ArtistService(
    private val repository: ArtistRepository,
    private val s3Service: S3Service
) {
    
    /**
     * Crear artista con imagen (sube a S3 automáticamente)
     * @param name Nombre del artista
     * @param biography Biografía
     * @param country País
     * @param imageBytes Bytes de la imagen
     * @return ArtistResponse con URL firmada de la imagen
     */
    suspend fun createArtist(
        name: String,
        biography: String?,
        country: String?,
        imageBytes: ByteArray
    ): ArtistResponse {
        // 1. Subir imagen a S3 y obtener KEY
        val imageKey = s3Service.uploadFile("artist-$name.jpg", imageBytes, "image/jpeg")
        
        // 2. Guardar en BD con la KEY
        val artist = repository.create(
            name = name,
            biography = biography,
            country = country,
            imageUrl = imageKey  // Guardamos solo la KEY
        )
        
        // 3. Generar URL firmada para devolver al cliente
        val signedUrl = s3Service.getPresignedUrl(imageKey)
        
        return artist.toResponse(signedUrl)
    }
    
    suspend fun createArtist(request: ArtistRequest): ArtistResponse {
        val artist = repository.create(
            name = request.name,
            biography = request.biography,
            country = request.country,
            imageUrl = request.imageUrl
        )
        return artist.toResponse()
    }
    
    suspend fun getArtistById(id: String): ArtistResponse? {
        return try {
            val uuid = UUID.fromString(id)
            val artist = repository.findById(uuid) ?: return null
            val signedUrl = artist.imageUrl?.let { key ->
                if (key.startsWith("http")) key else s3Service.getPresignedUrl(key)
            }
            artist.toResponse(signedUrl)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
    
    suspend fun getAllArtists(limit: Int = 100, offset: Int = 0): List<ArtistResponse> {
        val artists = repository.findAll(limit, offset)
        return artists.map { artist ->
            val signedUrl = artist.imageUrl?.let { key ->
                if (key.startsWith("http")) key else s3Service.getPresignedUrl(key)
            }
            artist.toResponse(signedUrl)
        }
    }
    
    suspend fun updateArtist(id: String, request: ArtistUpdateRequest): Boolean {
        return try {
            val uuid = UUID.fromString(id)
            val exists = repository.findById(uuid) ?: return false
            repository.update(
                id = uuid,
                name = request.name,
                biography = request.biography,
                country = request.country,
                imageUrl = request.imageUrl
            )
        } catch (e: IllegalArgumentException) {
            false
        }
    }
    
    suspend fun deleteArtist(id: String): Boolean {
        return try {
            val uuid = UUID.fromString(id)
            repository.delete(uuid)
        } catch (e: IllegalArgumentException) {
            false
        }
    }
    
    suspend fun searchArtists(name: String): List<ArtistResponse> {
        val artists = repository.searchByName(name)
        return artists.map { artist ->
            val signedUrl = artist.imageUrl?.let { key ->
                if (key.startsWith("http")) key else s3Service.getPresignedUrl(key)
            }
            artist.toResponse(signedUrl)
        }
    }
    
    private fun Artist.toResponse(signedImageUrl: String? = null) = ArtistResponse(
        id = id.toString(),
        name = name,
        biography = biography,
        country = country,
        imageUrl = signedImageUrl ?: imageUrl,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
    
    private fun Artist.toResponse() = ArtistResponse(
        id = id.toString(),
        name = name,
        biography = biography,
        country = country,
        imageUrl = imageUrl,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
