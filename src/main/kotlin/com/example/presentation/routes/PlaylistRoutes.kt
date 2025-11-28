package com.example.presentation.routes

import com.example.presentation.dto.*
import com.example.service.PlaylistService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Rutas REST para Playlists
 */
fun Route.playlistRoutes(service: PlaylistService) {
    route("/playlists") {
        // GET /playlists - Obtener todas las playlists
        get {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 100
            val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0
            val playlists = service.getAllPlaylists(limit, offset)
            call.respond(HttpStatusCode.OK, playlists)
        }
        
        // GET /playlists/{id} - Obtener playlist por ID
        get("/{id}") {
            val id = call.parameters["id"]
            if (id.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                    error = "Bad Request",
                    message = "Invalid playlist ID",
                    status = 400
                ))
                return@get
            }
            
            val playlist = service.getPlaylistById(id)
            if (playlist == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse(
                    error = "Not Found",
                    message = "Playlist with ID $id not found",
                    status = 404
                ))
            } else {
                call.respond(HttpStatusCode.OK, playlist)
            }
        }
        
        // POST /playlists - Crear nueva playlist
        post {
            val request = call.receive<PlaylistRequest>()
            val playlist = service.createPlaylist(request)
            if (playlist == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                    error = "Bad Request",
                    message = "Invalid user ID",
                    status = 400
                ))
            } else {
                call.respond(HttpStatusCode.Created, playlist)
            }
        }
        
        // PUT /playlists/{id} - Actualizar playlist
        put("/{id}") {
            val id = call.parameters["id"]
            if (id.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                    error = "Bad Request",
                    message = "Invalid playlist ID",
                    status = 400
                ))
                return@put
            }
            
            val request = call.receive<PlaylistUpdateRequest>()
            val updated = service.updatePlaylist(id, request)
            if (updated) {
                call.respond(HttpStatusCode.OK, SuccessResponse(
                    message = "Playlist updated successfully"
                ))
            } else {
                call.respond(HttpStatusCode.NotFound, ErrorResponse(
                    error = "Not Found",
                    message = "Playlist with ID $id not found",
                    status = 404
                ))
            }
        }
        
        // DELETE /playlists/{id} - Eliminar playlist
        delete("/{id}") {
            val id = call.parameters["id"]
            if (id.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                    error = "Bad Request",
                    message = "Invalid playlist ID",
                    status = 400
                ))
                return@delete
            }
            
            val deleted = service.deletePlaylist(id)
            if (deleted) {
                call.respond(HttpStatusCode.OK, SuccessResponse(
                    message = "Playlist deleted successfully"
                ))
            } else {
                call.respond(HttpStatusCode.NotFound, ErrorResponse(
                    error = "Not Found",
                    message = "Playlist with ID $id not found",
                    status = 404
                ))
            }
        }
        
        // GET /playlists/{id}/songs - Obtener canciones de una playlist
        get("/{id}/songs") {
            val id = call.parameters["id"]
            if (id.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                    error = "Bad Request",
                    message = "Invalid playlist ID",
                    status = 400
                ))
                return@get
            }
            
            val songs = service.getPlaylistSongs(id)
            if (songs == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                    error = "Bad Request",
                    message = "Invalid playlist ID",
                    status = 400
                ))
            } else {
                call.respond(HttpStatusCode.OK, songs)
            }
        }
        
        // POST /playlists/{id}/songs - Agregar canción a playlist
        post("/{id}/songs") {
            val id = call.parameters["id"]
            if (id.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                    error = "Bad Request",
                    message = "Invalid playlist ID",
                    status = 400
                ))
                return@post
            }
            
            val request = call.receive<AddSongToPlaylistRequest>()
            val added = service.addSongToPlaylist(id, request)
            if (added) {
                call.respond(HttpStatusCode.OK, SuccessResponse(
                    message = "Song added to playlist successfully"
                ))
            } else {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                    error = "Bad Request",
                    message = "Could not add song to playlist",
                    status = 400
                ))
            }
        }
        
        // DELETE /playlists/{id}/songs/{songId} - Remover canción de playlist
        delete("/{id}/songs/{songId}") {
            val id = call.parameters["id"]
            val songId = call.parameters["songId"]
            
            if (id.isNullOrBlank() || songId.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                    error = "Bad Request",
                    message = "Invalid playlist or song ID",
                    status = 400
                ))
                return@delete
            }
            
            val removed = service.removeSongFromPlaylist(id, songId)
            if (removed) {
                call.respond(HttpStatusCode.OK, SuccessResponse(
                    message = "Song removed from playlist successfully"
                ))
            } else {
                call.respond(HttpStatusCode.NotFound, ErrorResponse(
                    error = "Not Found",
                    message = "Song not found in playlist",
                    status = 404
                ))
            }
        }
    }
    
    // GET /users/{userId}/playlists - Obtener playlists de un usuario
    route("/users/{userId}/playlists") {
        get {
            val userId = call.parameters["userId"]
            if (userId.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                    error = "Bad Request",
                    message = "Invalid user ID",
                    status = 400
                ))
                return@get
            }
            
            val playlists = service.getPlaylistsByUser(userId)
            if (playlists == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                    error = "Bad Request",
                    message = "Invalid user ID",
                    status = 400
                ))
            } else {
                call.respond(HttpStatusCode.OK, playlists)
            }
        }
    }
}
