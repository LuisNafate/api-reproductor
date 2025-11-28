package com.example.data.database

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

/**
 * Tabla de base de datos para Playlists
 */
object PlaylistsTable : UUIDTable("playlists") {
    val name = varchar("name", 255)
    val description = text("description").nullable()
    val userId = reference("user_id", UsersTable.id)
    val isPublic = bool("is_public").default(true)
    val coverImageUrl = varchar("cover_image_url", 500).nullable()
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}

/**
 * Tabla intermedia para la relación muchos a muchos entre Playlists y Songs
 */
object PlaylistSongsTable : Table("playlist_songs") {
    val playlistId = reference("playlist_id", PlaylistsTable)
    val songId = reference("song_id", SongsTable)
    val position = integer("position")
    val addedAt = datetime("added_at")
    
    override val primaryKey = PrimaryKey(playlistId, songId)
}
