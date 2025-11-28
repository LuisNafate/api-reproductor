package com.example.data.repositories

import com.example.data.database.ArtistsTable
import com.example.domain.models.Artist
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime
import java.util.UUID

/**
 * Implementación del repositorio de Artistas usando Exposed
 */
class ArtistRepositoryImpl : ArtistRepository {
    
    private fun rowToArtist(row: ResultRow): Artist = Artist(
        id = row[ArtistsTable.id].value,
        name = row[ArtistsTable.name],
        biography = row[ArtistsTable.biography],
        country = row[ArtistsTable.country],
        imageUrl = row[ArtistsTable.imageUrl],
        createdAt = row[ArtistsTable.createdAt],
        updatedAt = row[ArtistsTable.updatedAt]
    )
    
    override suspend fun create(name: String, biography: String?, country: String?, imageUrl: String?): Artist =
        newSuspendedTransaction {
            val now = LocalDateTime.now()
            val artistId = UUID.randomUUID()
            ArtistsTable.insert {
                it[id] = artistId
                it[ArtistsTable.name] = name
                it[ArtistsTable.biography] = biography
                it[ArtistsTable.country] = country
                it[ArtistsTable.imageUrl] = imageUrl
                it[createdAt] = now
                it[updatedAt] = now
            }
            
            Artist(
                id = artistId,
                name = name,
                biography = biography,
                country = country,
                imageUrl = imageUrl,
                createdAt = now,
                updatedAt = now
            )
        }
    
    override suspend fun findById(id: UUID): Artist? = newSuspendedTransaction {
        ArtistsTable.selectAll()
            .where { ArtistsTable.id eq id }
            .map(::rowToArtist)
            .singleOrNull()
    }
    
    override suspend fun findAll(limit: Int, offset: Int): List<Artist> = newSuspendedTransaction {
        ArtistsTable.selectAll()
            .limit(limit, offset.toLong())
            .map(::rowToArtist)
    }
    
    override suspend fun update(id: UUID, name: String?, biography: String?, country: String?, imageUrl: String?): Boolean =
        newSuspendedTransaction {
            val updated = ArtistsTable.update({ ArtistsTable.id eq id }) {
                name?.let { n -> it[ArtistsTable.name] = n }
                if (biography != null) it[ArtistsTable.biography] = biography
                if (country != null) it[ArtistsTable.country] = country
                if (imageUrl != null) it[ArtistsTable.imageUrl] = imageUrl
                it[updatedAt] = LocalDateTime.now()
            }
            updated > 0
        }
    
    override suspend fun delete(id: UUID): Boolean = newSuspendedTransaction {
        ArtistsTable.deleteWhere { ArtistsTable.id eq id } > 0
    }
    
    override suspend fun searchByName(name: String): List<Artist> = newSuspendedTransaction {
        ArtistsTable.selectAll()
            .where { ArtistsTable.name.lowerCase() like "%${name.lowercase()}%" }
            .map(::rowToArtist)
    }
}
