package com.example.data.repositories

import com.example.data.database.PlaylistSongsTable
import com.example.data.database.PlaylistsTable
import com.example.data.database.SongsTable
import com.example.domain.models.Playlist
import com.example.domain.models.Song
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime
import java.util.UUID

/**
 * Implementación del repositorio de Playlists
 */
class PlaylistRepositoryImpl : PlaylistRepository {
    
    private fun rowToPlaylist(row: ResultRow): Playlist = Playlist(
        id = row[PlaylistsTable.id].value,
        name = row[PlaylistsTable.name],
        description = row[PlaylistsTable.description],
        userId = row[PlaylistsTable.userId].value,
        isPublic = row[PlaylistsTable.isPublic],
        coverImageUrl = row[PlaylistsTable.coverImageUrl],
        createdAt = row[PlaylistsTable.createdAt],
        updatedAt = row[PlaylistsTable.updatedAt]
    )
    
    private fun rowToSong(row: ResultRow): Song = Song(
        id = row[SongsTable.id].value,
        title = row[SongsTable.title],
        artistId = row[SongsTable.artistId].value,
        albumId = row[SongsTable.albumId]?.value,
        durationSeconds = row[SongsTable.durationSeconds],
        fileUrl = row[SongsTable.fileUrl],
        genre = row[SongsTable.genre],
        playCount = row[SongsTable.playCount],
        createdAt = row[SongsTable.createdAt],
        updatedAt = row[SongsTable.updatedAt]
    )
    
    override suspend fun create(name: String, description: String?, userId: UUID, isPublic: Boolean, coverImageUrl: String?): Playlist =
        newSuspendedTransaction {
            val now = LocalDateTime.now()
            val playlistId = UUID.randomUUID()
            PlaylistsTable.insert {
                it[id] = playlistId
                it[PlaylistsTable.name] = name
                it[PlaylistsTable.description] = description
                it[PlaylistsTable.userId] = userId
                it[PlaylistsTable.isPublic] = isPublic
                it[PlaylistsTable.coverImageUrl] = coverImageUrl
                it[createdAt] = now
                it[updatedAt] = now
            }
            
            Playlist(
                id = playlistId,
                name = name,
                description = description,
                userId = userId,
                isPublic = isPublic,
                coverImageUrl = coverImageUrl,
                createdAt = now,
                updatedAt = now
            )
        }
    
    override suspend fun findById(id: UUID): Playlist? = newSuspendedTransaction {
        PlaylistsTable.selectAll()
            .where { PlaylistsTable.id eq id }
            .map(::rowToPlaylist)
            .singleOrNull()
    }
    
    override suspend fun findAll(limit: Int, offset: Int): List<Playlist> = newSuspendedTransaction {
        PlaylistsTable.selectAll()
            .limit(limit, offset.toLong())
            .map(::rowToPlaylist)
    }
    
    override suspend fun findByUserId(userId: UUID): List<Playlist> = newSuspendedTransaction {
        PlaylistsTable.selectAll()
            .where { PlaylistsTable.userId eq userId }
            .map(::rowToPlaylist)
    }
    
    override suspend fun update(id: UUID, name: String?, description: String?, isPublic: Boolean?, coverImageUrl: String?): Boolean =
        newSuspendedTransaction {
            val updated = PlaylistsTable.update({ PlaylistsTable.id eq id }) {
                name?.let { n -> it[PlaylistsTable.name] = n }
                if (description != null) it[PlaylistsTable.description] = description
                isPublic?.let { p -> it[PlaylistsTable.isPublic] = p }
                if (coverImageUrl != null) it[PlaylistsTable.coverImageUrl] = coverImageUrl
                it[updatedAt] = LocalDateTime.now()
            }
            updated > 0
        }
    
    override suspend fun delete(id: UUID): Boolean = newSuspendedTransaction {
        // Primero eliminar las canciones de la playlist
        PlaylistSongsTable.deleteWhere { playlistId eq id }
        // Luego eliminar la playlist
        PlaylistsTable.deleteWhere { PlaylistsTable.id eq id } > 0
    }
    
    override suspend fun addSong(playlistId: UUID, songId: UUID, position: Int): Boolean = newSuspendedTransaction {
        try {
            PlaylistSongsTable.insert {
                it[PlaylistSongsTable.playlistId] = playlistId
                it[PlaylistSongsTable.songId] = songId
                it[PlaylistSongsTable.position] = position
                it[addedAt] = LocalDateTime.now()
            }
            true
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun removeSong(playlistId: UUID, songId: UUID): Boolean = newSuspendedTransaction {
        PlaylistSongsTable.deleteWhere {
            (PlaylistSongsTable.playlistId eq playlistId) and (PlaylistSongsTable.songId eq songId)
        } > 0
    }
    
    override suspend fun getSongs(playlistId: UUID): List<Song> = newSuspendedTransaction {
        (PlaylistSongsTable innerJoin SongsTable)
            .select(SongsTable.columns)
            .where { PlaylistSongsTable.playlistId eq playlistId }
            .orderBy(PlaylistSongsTable.position to SortOrder.ASC)
            .map(::rowToSong)
    }
}
