package com.example.config

import com.example.data.database.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Configuración y gestión de la conexión a la base de datos
 */
object DatabaseFactory {
    
    fun init() {
        val database = try {
            Database.connect(createHikariDataSource())
        } catch (e: Exception) {
            // Si falla PostgreSQL, usar H2 en memoria
            println("PostgreSQL connection failed, using H2 in-memory database for development")
            initH2()
            return
        }
        
        // Crear tablas si no existen
        transaction(database) {
            SchemaUtils.create(
                ArtistsTable,
                AlbumsTable,
                SongsTable,
                PlaylistsTable,
                PlaylistSongsTable,
                UsersTable // Nueva tabla
            )
        }
    }
    
    private fun createHikariDataSource(): HikariDataSource {
        // Leer valores del archivo .env o usar defaults
        val dbHost = EnvLoader.get("DB_HOST", "localhost")!!
        val dbPort = EnvLoader.get("DB_PORT", "5432")!!
        val dbName = EnvLoader.get("DB_NAME", "music_player")!!
        val dbUser = EnvLoader.get("DB_USER", "music_admin")!!
        val dbPassword = EnvLoader.get("DB_PASSWORD", "music1234")!!
        
        val jdbcUrl = "jdbc:postgresql://$dbHost:$dbPort/$dbName"
        
        println("Connecting to database: $jdbcUrl")
        
        val config = HikariConfig().apply {
            // Configuración para PostgreSQL
            driverClassName = "org.postgresql.Driver"
            this.jdbcUrl = jdbcUrl
            username = dbUser
            password = dbPassword
            
            // Configuración del pool de conexiones
            maximumPoolSize = EnvLoader.get("DB_MAX_POOL_SIZE")?.toIntOrNull() ?: 10
            minimumIdle = EnvLoader.get("DB_MIN_IDLE")?.toIntOrNull() ?: 2
            idleTimeout = 600000 // 10 minutos
            connectionTimeout = EnvLoader.get("DB_CONNECTION_TIMEOUT")?.toLongOrNull() ?: 30000
            maxLifetime = 1800000 // 30 minutos
            
            // Validación de conexiones
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        
        return HikariDataSource(config)
    }
    
    /**
     * Configuración alternativa para H2 (útil para testing)
     */
    private fun initH2(): Database {
        val database = Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
            driver = "org.h2.Driver",
            user = "sa",
            password = ""
        )
        
        transaction(database) {
            SchemaUtils.create(
                ArtistsTable,
                AlbumsTable,
                SongsTable,
                PlaylistsTable,
                PlaylistSongsTable,
                UsersTable
            )
        }
        
        return database
    }
}
