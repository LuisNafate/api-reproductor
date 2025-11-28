package com.example.service

import com.example.data.repositories.PlaylistRepository
import com.example.domain.models.Playlist
import com.example.domain.models.Song
import com.example.presentation.dto.*
import java.util.UUID

/**
 * Servicio para la lógica de negocio de Playlists
 */
class PlaylistService(private val repository: PlaylistRepository) {
    
    suspend fun createPlaylist(request: PlaylistRequest): PlaylistResponse? {
        return try {
            val userUuid = UUID.fromString(request.userId)
            val playlist = repository.create(
                name = request.name,
                description = request.description,
                userId = userUuid,
                isPublic = request.isPublic,
                coverImageUrl = request.coverImageUrl
            )
            playlist.toResponse()
        } catch (e: IllegalArgumentException) {
            null
        }
    }
    
    suspend fun getPlaylistById(id: String): PlaylistResponse? {
        return try {
            val uuid = UUID.fromString(id)
            repository.findById(uuid)?.toResponse()
        } catch (e: IllegalArgumentException) {
            null
        }
    }
    
    suspend fun getAllPlaylists(limit: Int = 100, offset: Int = 0): List<PlaylistResponse> {
        return repository.findAll(limit, offset).map { it.toResponse() }
    }
    
    suspend fun getPlaylistsByUser(userId: String): List<PlaylistResponse>? {
        return try {
            val uuid = UUID.fromString(userId)
            repository.findByUserId(uuid).map { it.toResponse() }
        } catch (e: IllegalArgumentException) {
            null
        }
    }
    
    suspend fun updatePlaylist(id: String, request: PlaylistUpdateRequest): Boolean {
        return try {
            val uuid = UUID.fromString(id)
            val exists = repository.findById(uuid) ?: return false
            repository.update(
                id = uuid,
                name = request.name,
                description = request.description,
                isPublic = request.isPublic,
                coverImageUrl = request.coverImageUrl
            )
        } catch (e: IllegalArgumentException) {
            false
        }
    }
    
    suspend fun deletePlaylist(id: String): Boolean {
        return try {
            val uuid = UUID.fromString(id)
            repository.delete(uuid)
        } catch (e: IllegalArgumentException) {
            false
        }
    }
    
    suspend fun addSongToPlaylist(playlistId: String, request: AddSongToPlaylistRequest): Boolean {
        return try {
            val playlistUuid = UUID.fromString(playlistId)
            val songUuid = UUID.fromString(request.songId)
            // Si no se especifica posición, obtener el número de canciones actual + 1
            val position = request.position ?: (repository.getSongs(playlistUuid).size + 1)
            repository.addSong(playlistUuid, songUuid, position)
        } catch (e: IllegalArgumentException) {
            false
        }
    }
    
    suspend fun removeSongFromPlaylist(playlistId: String, songId: String): Boolean {
        return try {
            val playlistUuid = UUID.fromString(playlistId)
            val songUuid = UUID.fromString(songId)
            repository.removeSong(playlistUuid, songUuid)
        } catch (e: IllegalArgumentException) {
            false
        }
    }
    
    suspend fun getPlaylistSongs(playlistId: String): List<SongResponse>? {
        return try {
            val uuid = UUID.fromString(playlistId)
            repository.getSongs(uuid).map { it.toSongResponse() }
        } catch (e: IllegalArgumentException) {
            null
        }
    }
    
    private fun Playlist.toResponse() = PlaylistResponse(
        id = id.toString(),
        name = name,
        description = description,
        userId = userId.toString(),
        isPublic = isPublic,
        coverImageUrl = coverImageUrl,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
    
    private fun Song.toSongResponse() = SongResponse(
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
}
