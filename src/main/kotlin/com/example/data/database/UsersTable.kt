package com.example.data.database

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

/**
 * Tabla de usuarios para autenticación
 */
object UsersTable : UUIDTable("users") {
    val username = varchar("username", 50).uniqueIndex()
    val password = varchar("password", 255) // BCrypt hash
    val email = varchar("email", 100).uniqueIndex()
    val role = varchar("role", 20).default("USER") // USER, ADMIN
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
}
