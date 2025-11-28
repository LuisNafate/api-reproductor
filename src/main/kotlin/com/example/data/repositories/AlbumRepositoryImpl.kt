package com.example.data.repositories

import com.example.data.database.AlbumsTable
import com.example.domain.models.Album
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

/**
 * Implementación del repositorio de Álbumes
 */
class AlbumRepositoryImpl : AlbumRepository {
    
    private fun rowToAlbum(row: ResultRow): Album = Album(
        id = row[AlbumsTable.id].value,
        title = row[AlbumsTable.title],
        artistId = row[AlbumsTable.artistId].value,
        releaseDate = row[AlbumsTable.releaseDate],
        coverImageUrl = row[AlbumsTable.coverImageUrl],
        genre = row[AlbumsTable.genre],
        createdAt = row[AlbumsTable.createdAt],
        updatedAt = row[AlbumsTable.updatedAt]
    )
    
    override suspend fun create(title: String, artistId: UUID, releaseDate: LocalDate?, coverImageUrl: String?, genre: String?): Album =
        newSuspendedTransaction {
            val now = LocalDateTime.now()
            val albumId = UUID.randomUUID()
            AlbumsTable.insert {
                it[id] = albumId
                it[AlbumsTable.title] = title
                it[AlbumsTable.artistId] = artistId
                it[AlbumsTable.releaseDate] = releaseDate
                it[AlbumsTable.coverImageUrl] = coverImageUrl
                it[AlbumsTable.genre] = genre
                it[createdAt] = now
                it[updatedAt] = now
            }
            
            Album(
                id = albumId,
                title = title,
                artistId = artistId,
                releaseDate = releaseDate,
                coverImageUrl = coverImageUrl,
                genre = genre,
                createdAt = now,
                updatedAt = now
            )
        }
    
    override suspend fun findById(id: UUID): Album? = newSuspendedTransaction {
        AlbumsTable.selectAll()
            .where { AlbumsTable.id eq id }
            .map(::rowToAlbum)
            .singleOrNull()
    }
    
    override suspend fun findAll(limit: Int, offset: Int): List<Album> = newSuspendedTransaction {
        AlbumsTable.selectAll()
            .limit(limit, offset.toLong())
            .map(::rowToAlbum)
    }
    
    override suspend fun findByArtistId(artistId: UUID): List<Album> = newSuspendedTransaction {
        AlbumsTable.selectAll()
            .where { AlbumsTable.artistId eq artistId }
            .map(::rowToAlbum)
    }
    
    override suspend fun update(id: UUID, title: String?, artistId: UUID?, releaseDate: LocalDate?, coverImageUrl: String?, genre: String?): Boolean =
        newSuspendedTransaction {
            val updated = AlbumsTable.update({ AlbumsTable.id eq id }) {
                title?.let { t -> it[AlbumsTable.title] = t }
                artistId?.let { a -> it[AlbumsTable.artistId] = a }
                if (releaseDate != null) it[AlbumsTable.releaseDate] = releaseDate
                if (coverImageUrl != null) it[AlbumsTable.coverImageUrl] = coverImageUrl
                if (genre != null) it[AlbumsTable.genre] = genre
                it[updatedAt] = LocalDateTime.now()
            }
            updated > 0
        }
    
    override suspend fun delete(id: UUID): Boolean = newSuspendedTransaction {
        AlbumsTable.deleteWhere { AlbumsTable.id eq id } > 0
    }
}
