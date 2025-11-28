package com.example.data.repositories

import com.example.data.database.SongsTable
import com.example.domain.models.Song
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime
import java.util.UUID

/**
 * Implementación del repositorio de Canciones
 */
class SongRepositoryImpl : SongRepository {
    
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
    
    override suspend fun create(title: String, artistId: UUID, albumId: UUID?, durationSeconds: Int, fileUrl: String, genre: String?): Song =
        newSuspendedTransaction {
            val now = LocalDateTime.now()
            val songId = UUID.randomUUID()
            SongsTable.insert {
                it[id] = songId
                it[SongsTable.title] = title
                it[SongsTable.artistId] = artistId
                it[SongsTable.albumId] = albumId
                it[SongsTable.durationSeconds] = durationSeconds
                it[SongsTable.fileUrl] = fileUrl
                it[SongsTable.genre] = genre
                it[playCount] = 0
                it[createdAt] = now
                it[updatedAt] = now
            }
            
            Song(
                id = songId,
                title = title,
                artistId = artistId,
                albumId = albumId,
                durationSeconds = durationSeconds,
                fileUrl = fileUrl,
                genre = genre,
                playCount = 0,
                createdAt = now,
                updatedAt = now
            )
        }
    
    override suspend fun findById(id: UUID): Song? = newSuspendedTransaction {
        SongsTable.selectAll()
            .where { SongsTable.id eq id }
            .map(::rowToSong)
            .singleOrNull()
    }
    
    override suspend fun findAll(limit: Int, offset: Int): List<Song> = newSuspendedTransaction {
        SongsTable.selectAll()
            .limit(limit, offset.toLong())
            .map(::rowToSong)
    }
    
    override suspend fun findByArtistId(artistId: UUID): List<Song> = newSuspendedTransaction {
        SongsTable.selectAll()
            .where { SongsTable.artistId eq artistId }
            .map(::rowToSong)
    }
    
    override suspend fun findByAlbumId(albumId: UUID): List<Song> = newSuspendedTransaction {
        SongsTable.selectAll()
            .where { SongsTable.albumId eq albumId }
            .map(::rowToSong)
    }
    
    override suspend fun update(id: UUID, title: String?, artistId: UUID?, albumId: UUID?, durationSeconds: Int?, fileUrl: String?, genre: String?): Boolean =
        newSuspendedTransaction {
            val updated = SongsTable.update({ SongsTable.id eq id }) {
                title?.let { t -> it[SongsTable.title] = t }
                artistId?.let { a -> it[SongsTable.artistId] = a }
                if (albumId != null) it[SongsTable.albumId] = albumId
                durationSeconds?.let { d -> it[SongsTable.durationSeconds] = d }
                fileUrl?.let { f -> it[SongsTable.fileUrl] = f }
                if (genre != null) it[SongsTable.genre] = genre
                it[updatedAt] = LocalDateTime.now()
            }
            updated > 0
        }
    
    override suspend fun delete(id: UUID): Boolean = newSuspendedTransaction {
        SongsTable.deleteWhere { SongsTable.id eq id } > 0
    }
    
    override suspend fun incrementPlayCount(id: UUID): Boolean = newSuspendedTransaction {
        val song = findById(id) ?: return@newSuspendedTransaction false
        SongsTable.update({ SongsTable.id eq id }) {
            it[playCount] = song.playCount + 1
            it[updatedAt] = LocalDateTime.now()
        } > 0
    }
    
    override suspend fun searchByTitle(title: String): List<Song> = newSuspendedTransaction {
        SongsTable.selectAll()
            .where { SongsTable.title.lowerCase() like "%${title.lowercase()}%" }
            .map(::rowToSong)
    }
}
