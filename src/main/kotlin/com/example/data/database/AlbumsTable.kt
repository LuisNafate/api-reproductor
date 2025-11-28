package com.example.data.database

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime

/**
 * Tabla de base de datos para Álbumes
 */
object AlbumsTable : UUIDTable("albums") {
    val title = varchar("title", 255)
    val artistId = reference("artist_id", ArtistsTable)
    val releaseDate = date("release_date").nullable()
    val coverImageUrl = varchar("cover_image_url", 500).nullable()
    val genre = varchar("genre", 100).nullable()
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}
