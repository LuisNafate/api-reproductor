package com.example.data.database

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime

/**
 * Tabla de base de datos para Canciones
 */
object SongsTable : UUIDTable("songs") {
    val title = varchar("title", 255)
    val artistId = reference("artist_id", ArtistsTable)
    val albumId = reference("album_id", AlbumsTable).nullable()
    val durationSeconds = integer("duration_seconds")
    val fileUrl = varchar("file_url", 500)
    val genre = varchar("genre", 100).nullable()
    val playCount = integer("play_count").default(0)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}
