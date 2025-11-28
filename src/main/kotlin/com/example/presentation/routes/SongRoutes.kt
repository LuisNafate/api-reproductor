package com.example.presentation.routes

import com.example.presentation.dto.*
import com.example.service.SongService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Rutas REST para Songs
 */
fun Route.songRoutes(service: SongService) {
    route("/songs") {
        // GET /songs - Obtener todas las canciones
        get {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 100
            val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0
            val songs = service.getAllSongs(limit, offset)
            call.respond(HttpStatusCode.OK, songs)
        }
        
        // GET /songs/search?title=... - Buscar canciones por título
        get("/search") {
            val title = call.request.queryParameters["title"]
            if (title.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                    error = "Bad Request",
                    message = "Query parameter 'title' is required",
                    status = 400
                ))
                return@get
            }
            val songs = service.searchSongs(title)
            call.respond(HttpStatusCode.OK, songs)
        }
        
        // GET /songs/{id} - Obtener canción por ID
        get("/{id}") {
            val id = call.parameters["id"]
            if (id.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                    error = "Bad Request",
                    message = "Invalid song ID",
                    status = 400
                ))
                return@get
            }
            
            val song = service.getSongById(id)
            if (song == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse(
                    error = "Not Found",
                    message = "Song with ID $id not found",
                    status = 404
                ))
            } else {
                call.respond(HttpStatusCode.OK, song)
            }
        }
        
        // POST /songs - Crear nueva canción
        post {
            val request = call.receive<SongRequest>()
            val song = service.createSong(request)
            if (song == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                    error = "Bad Request",
                    message = "Invalid artist or album ID",
                    status = 400
                ))
            } else {
                call.respond(HttpStatusCode.Created, song)
            }
        }
        
        // PUT /songs/{id} - Actualizar canción
        put("/{id}") {
            val id = call.parameters["id"]
            if (id.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                    error = "Bad Request",
                    message = "Invalid song ID",
                    status = 400
                ))
                return@put
            }
            
            val request = call.receive<SongUpdateRequest>()
            val updated = service.updateSong(id, request)
            if (updated) {
                call.respond(HttpStatusCode.OK, SuccessResponse(
                    message = "Song updated successfully"
                ))
            } else {
                call.respond(HttpStatusCode.NotFound, ErrorResponse(
                    error = "Not Found",
                    message = "Song with ID $id not found",
                    status = 404
                ))
            }
        }
        
        // DELETE /songs/{id} - Eliminar canción
        delete("/{id}") {
            val id = call.parameters["id"]
            if (id.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                    error = "Bad Request",
                    message = "Invalid song ID",
                    status = 400
                ))
                return@delete
            }
            
            val deleted = service.deleteSong(id)
            if (deleted) {
                call.respond(HttpStatusCode.OK, SuccessResponse(
                    message = "Song deleted successfully"
                ))
            } else {
                call.respond(HttpStatusCode.NotFound, ErrorResponse(
                    error = "Not Found",
                    message = "Song with ID $id not found",
                    status = 404
                ))
            }
        }
        
        // POST /songs/{id}/play - Incrementar contador de reproducciones
        post("/{id}/play") {
            val id = call.parameters["id"]
            if (id.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                    error = "Bad Request",
                    message = "Invalid song ID",
                    status = 400
                ))
                return@post
            }
            
            val played = service.playSong(id)
            if (played) {
                call.respond(HttpStatusCode.OK, SuccessResponse(
                    message = "Play count incremented"
                ))
            } else {
                call.respond(HttpStatusCode.NotFound, ErrorResponse(
                    error = "Not Found",
                    message = "Song with ID $id not found",
                    status = 404
                ))
            }
        }
    }
    
    // GET /artists/{artistId}/songs - Obtener canciones de un artista
    route("/artists/{artistId}/songs") {
        get {
            val artistId = call.parameters["artistId"]
            if (artistId.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                    error = "Bad Request",
                    message = "Invalid artist ID",
                    status = 400
                ))
                return@get
            }
            
            val songs = service.getSongsByArtist(artistId)
            if (songs == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                    error = "Bad Request",
                    message = "Invalid artist ID",
                    status = 400
                ))
            } else {
                call.respond(HttpStatusCode.OK, songs)
            }
        }
    }
    
    // GET /albums/{albumId}/songs - Obtener canciones de un álbum
    route("/albums/{albumId}/songs") {
        get {
            val albumId = call.parameters["albumId"]
            if (albumId.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                    error = "Bad Request",
                    message = "Invalid album ID",
                    status = 400
                ))
                return@get
            }
            
            val songs = service.getSongsByAlbum(albumId)
            if (songs == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                    error = "Bad Request",
                    message = "Invalid album ID",
                    status = 400
                ))
            } else {
                call.respond(HttpStatusCode.OK, songs)
            }
        }
    }
}
