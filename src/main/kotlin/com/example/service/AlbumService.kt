package com.example.service

import com.example.data.repositories.AlbumRepository
import com.example.domain.models.Album
import com.example.presentation.dto.AlbumRequest
import com.example.presentation.dto.AlbumResponse
import com.example.presentation.dto.AlbumUpdateRequest
import java.util.UUID

/**
 * Servicio para la lógica de negocio de Álbumes
 */
class AlbumService(
    private val repository: AlbumRepository,
    private val s3Service: S3Service
) {
    
    /**
     * Crear álbum con imagen (sube a S3 automáticamente)
     * @param title Título del álbum
     * @param year Año de lanzamiento
     * @param artistId UUID del artista
     * @param imageBytes Bytes de la imagen de portada
     * @return AlbumResponse con URL firmada de la imagen
     */
    suspend fun createAlbum(
        title: String,
        year: Int,
        artistId: UUID,
        imageBytes: ByteArray,
        genre: String? = null
    ): AlbumResponse {
        // 1. Subir imagen a S3 y obtener KEY
        val imageKey = s3Service.uploadFile("album-$title.jpg", imageBytes, "image/jpeg")
        
        // 2. Guardar en BD con la KEY
        val album = repository.create(
            title = title,
            artistId = artistId,
            releaseDate = null,  // Puedes ajustar esto
            coverImageUrl = imageKey,  // Guardamos solo la KEY
            genre = genre
        )
        
        // 3. Generar URL firmada para devolver al cliente
        val signedUrl = s3Service.getPresignedUrl(imageKey)
        
        return album.toResponse(signedUrl)
    }
    
    suspend fun createAlbum(request: AlbumRequest): AlbumResponse? {
        return try {
            val artistUuid = UUID.fromString(request.artistId)
            val album = repository.create(
                title = request.title,
                artistId = artistUuid,
                releaseDate = request.releaseDate,
                coverImageUrl = request.coverImageUrl,
                genre = request.genre
            )
            album.toResponse()
        } catch (e: IllegalArgumentException) {
            null
        }
    }
    
    suspend fun getAlbumById(id: String): AlbumResponse? {
        return try {
            val uuid = UUID.fromString(id)
            val album = repository.findById(uuid) ?: return null
            val signedUrl = album.coverImageUrl?.let { key ->
                if (key.startsWith("http")) key else s3Service.getPresignedUrl(key)
            }
            album.toResponse(signedUrl)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
    
    suspend fun getAllAlbums(limit: Int = 100, offset: Int = 0): List<AlbumResponse> {
        val albums = repository.findAll(limit, offset)
        return albums.map { album ->
            val signedUrl = album.coverImageUrl?.let { key ->
                if (key.startsWith("http")) key else s3Service.getPresignedUrl(key)
            }
            album.toResponse(signedUrl)
        }
    }
    
    suspend fun getAlbumsByArtist(artistId: String): List<AlbumResponse>? {
        return try {
            val uuid = UUID.fromString(artistId)
            val albums = repository.findByArtistId(uuid)
            albums.map { album ->
                val signedUrl = album.coverImageUrl?.let { key ->
                    if (key.startsWith("http")) key else s3Service.getPresignedUrl(key)
                }
                album.toResponse(signedUrl)
            }
        } catch (e: IllegalArgumentException) {
            null
        }
    }
    
    suspend fun updateAlbum(id: String, request: AlbumUpdateRequest): Boolean {
        return try {
            val uuid = UUID.fromString(id)
            val exists = repository.findById(uuid) ?: return false
            val artistUuid = request.artistId?.let { UUID.fromString(it) }
            repository.update(
                id = uuid,
                title = request.title,
                artistId = artistUuid,
                releaseDate = request.releaseDate,
                coverImageUrl = request.coverImageUrl,
                genre = request.genre
            )
        } catch (e: IllegalArgumentException) {
            false
        }
    }
    
    suspend fun deleteAlbum(id: String): Boolean {
        return try {
            val uuid = UUID.fromString(id)
            repository.delete(uuid)
        } catch (e: IllegalArgumentException) {
            false
        }
    }
    
    private fun Album.toResponse(signedImageUrl: String? = null) = AlbumResponse(
        id = id.toString(),
        title = title,
        artistId = artistId.toString(),
        releaseDate = releaseDate,
        coverImageUrl = signedImageUrl ?: coverImageUrl,
        genre = genre,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
    
    private fun Album.toResponse() = AlbumResponse(
        id = id.toString(),
        title = title,
        artistId = artistId.toString(),
        releaseDate = releaseDate,
        coverImageUrl = coverImageUrl,
        genre = genre,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
