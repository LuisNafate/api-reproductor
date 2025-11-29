package com.example

import com.example.data.repositories.*
import com.example.presentation.routes.*
import com.example.service.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    // Inicializar repositorios
    val artistRepository: ArtistRepository = ArtistRepositoryImpl()
    val albumRepository: AlbumRepository = AlbumRepositoryImpl()
    val songRepository: SongRepository = SongRepositoryImpl()
    val playlistRepository: PlaylistRepository = PlaylistRepositoryImpl()
    
    // Inicializar S3Service primero (necesario para Artist y Album)
    val s3Service = S3Service()
    
    // Inicializar servicios
    val artistService = ArtistService(artistRepository, s3Service)
    val albumService = AlbumService(albumRepository, s3Service)
    val songService = SongService(songRepository, s3Service)
    val playlistService = PlaylistService(playlistRepository)
    val authService = AuthService()
    
    routing {
        // Ruta de health check
        get("/") {
            call.respond(mapOf(
                "status" to "OK",
                "message" to "Music Player API",
                "version" to "1.0.0"
            ))
        }
        
        get("/health") {
            call.respond(mapOf(
                "status" to "healthy",
                "timestamp" to System.currentTimeMillis()
            ))
        }
        
        // Configurar rutas de la API
        route("/api/v1") {
            // Rutas de autenticación (públicas)
            authRoutes(authService)
            
            // Rutas de upload (protegidas con JWT)
            fileUploadRoutes(s3Service)
            
            // Rutas de recursos
            artistRoutes(artistService)
            albumRoutes(albumService)
            songRoutes(songService)
            playlistRoutes(playlistService)
        }
    }
}
