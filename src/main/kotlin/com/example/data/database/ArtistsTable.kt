package com.example.data.database

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime

/**
 * Tabla de base de datos para Artistas
 */
object ArtistsTable : UUIDTable("artists") {
    val name = varchar("name", 255)
    val biography = text("biography").nullable()
    val country = varchar("country", 100).nullable()
    val imageUrl = varchar("image_url", 500).nullable()
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}
