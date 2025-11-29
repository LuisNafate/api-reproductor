package com.example.service

import com.example.data.repositories.SongRepository
import com.example.domain.models.Song
import com.example.presentation.dto.SongRequest
import com.example.presentation.dto.SongResponse
import com.example.presentation.dto.SongUpdateRequest
import java.util.UUID

/**
 * Servicio para la lógica de negocio de Canciones
 */
class SongService(
    private val repository: SongRepository,
    private val s3Service: S3Service
) {
    
    /**
     * Crear canción con multipart upload (archivo de audio MP3)
     */
    suspend fun createSongWithAudio(
        title: String,
        artistId: UUID,
        albumId: UUID?,
        durationSeconds: Int,
        audioBytes: ByteArray,
        genre: String?
    ): SongResponse? {
        return try {
            // 1. Subir archivo MP3 a S3
            val audioKey = s3Service.uploadFile(
                fileName = "song-$title-${UUID.randomUUID()}.mp3",
                fileBytes = audioBytes,
                contentType = "audio/mpeg"
            )
            
            // 2. Crear canción en BD con la KEY de S3
            val song = repository.create(
                title = title,
                artistId = artistId,
                albumId = albumId,
                durationSeconds = durationSeconds,
                fileUrl = audioKey,
                genre = genre
            )
            
            // 3. Generar URL firmada para la respuesta
            val presignedUrl = s3Service.getPresignedUrl(audioKey)
            song.toResponseWithPresignedUrl(presignedUrl)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Crear canción con JSON (fileUrl ya debe estar en S3)
     */
    suspend fun createSong(request: SongRequest): SongResponse? {
        return try {
            val artistUuid = UUID.fromString(request.artistId)
            val albumUuid = request.albumId?.let { UUID.fromString(it) }
            val song = repository.create(
                title = request.title,
                artistId = artistUuid,
                albumId = albumUuid,
                durationSeconds = request.durationSeconds,
                fileUrl = request.fileUrl,
                genre = request.genre
            )
            song.toResponse()
        } catch (e: IllegalArgumentException) {
            null
        }
    }
    
    suspend fun getSongById(id: String): SongResponse? {
        return try {
            val uuid = UUID.fromString(id)
            repository.findById(uuid)?.toResponse()
        } catch (e: IllegalArgumentException) {
            null
        }
    }
    
    suspend fun getAllSongs(limit: Int = 100, offset: Int = 0): List<SongResponse> {
        return repository.findAll(limit, offset).map { it.toResponse() }
    }
    
    suspend fun getSongsByArtist(artistId: String): List<SongResponse>? {
        return try {
            val uuid = UUID.fromString(artistId)
            repository.findByArtistId(uuid).map { it.toResponse() }
        } catch (e: IllegalArgumentException) {
            null
        }
    }
    
    suspend fun getSongsByAlbum(albumId: String): List<SongResponse>? {
        return try {
            val uuid = UUID.fromString(albumId)
            repository.findByAlbumId(uuid).map { it.toResponse() }
        } catch (e: IllegalArgumentException) {
            null
        }
    }
    
    suspend fun updateSong(id: String, request: SongUpdateRequest): Boolean {
        return try {
            val uuid = UUID.fromString(id)
            val exists = repository.findById(uuid) ?: return false
            val artistUuid = request.artistId?.let { UUID.fromString(it) }
            val albumUuid = request.albumId?.let { UUID.fromString(it) }
            repository.update(
                id = uuid,
                title = request.title,
                artistId = artistUuid,
                albumId = albumUuid,
                durationSeconds = request.durationSeconds,
                fileUrl = request.fileUrl,
                genre = request.genre
            )
        } catch (e: IllegalArgumentException) {
            false
        }
    }
    
    suspend fun deleteSong(id: String): Boolean {
        return try {
            val uuid = UUID.fromString(id)
            repository.delete(uuid)
        } catch (e: IllegalArgumentException) {
            false
        }
    }
    
    suspend fun playSong(id: String): Boolean {
        return try {
            val uuid = UUID.fromString(id)
            repository.incrementPlayCount(uuid)
        } catch (e: IllegalArgumentException) {
            false
        }
    }
    
    suspend fun searchSongs(title: String): List<SongResponse> {
        return repository.searchByTitle(title).map { it.toResponse() }
    }
    
    private fun Song.toResponse() = SongResponse(
        id = id.toString(),
        title = title,
        artistId = artistId.toString(),
        albumId = albumId?.toString(),
        durationSeconds = durationSeconds,
        fileUrl = fileUrl,
        genre = genre,
        playCount = playCount,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
    
    private fun Song.toResponseWithPresignedUrl(presignedUrl: String) = SongResponse(
        id = id.toString(),
        title = title,
        artistId = artistId.toString(),
        albumId = albumId?.toString(),
        durationSeconds = durationSeconds,
        fileUrl = presignedUrl,  // URL firmada en lugar de KEY
        genre = genre,
        playCount = playCount,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
