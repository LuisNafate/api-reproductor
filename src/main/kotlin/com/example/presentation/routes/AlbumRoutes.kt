package com.example.presentation.routes

import com.example.presentation.dto.*
import com.example.service.AlbumService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

/**
 * Rutas REST para Albums
 */
fun Route.albumRoutes(service: AlbumService) {
    route("/albums") {
        // GET /albums - Obtener todos los álbumes
        get {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 100
            val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0
            val albums = service.getAllAlbums(limit, offset)
            call.respond(HttpStatusCode.OK, albums)
        }
        
        // GET /albums/{id} - Obtener álbum por ID
        get("/{id}") {
            val id = call.parameters["id"]
            if (id.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                    error = "Bad Request",
                    message = "Invalid album ID",
                    status = 400
                ))
                return@get
            }
            
            val album = service.getAlbumById(id)
            if (album == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse(
                    error = "Not Found",
                    message = "Album with ID $id not found",
                    status = 404
                ))
            } else {
                call.respond(HttpStatusCode.OK, album)
            }
        }
        
        // POST /albums - Crear nuevo álbum
        authenticate("auth-jwt") {
            post {
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString()
                
                // Solo ADMIN puede crear álbumes
                if (role != "ADMIN") {
                    call.respond(HttpStatusCode.Forbidden, ErrorResponse(
                        error = "Forbidden",
                        message = "Solo administradores pueden crear álbumes",
                        status = 403
                    ))
                    return@post
                }
                
                try {
                    // Procesar multipart/form-data
                    val multipart = call.receiveMultipart()
                    var title = ""
                    var year = 0
                    var artistId: UUID? = null
                    var genre: String? = null
                    var imageBytes: ByteArray? = null
                    
                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FormItem -> {
                                when (part.name) {
                                    "title" -> title = part.value
                                    "name" -> title = part.value  // Alternativa
                                    "year" -> year = part.value.toIntOrNull() ?: 0
                                    "artistId" -> artistId = try { UUID.fromString(part.value) } catch (e: Exception) { null }
                                    "genre" -> genre = part.value
                                }
                            }
                            is PartData.FileItem -> {
                                if (part.name == "image") {
                                    imageBytes = part.streamProvider().readBytes()
                                }
                            }
                            else -> {}
                        }
                        part.dispose()
                    }
                    
                    // Validación
                    if (title.isEmpty() || year == 0 || artistId == null || imageBytes == null) {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                            error = "Bad Request",
                            message = "Faltan datos obligatorios: title, year, artistId o image",
                            status = 400
                        ))
                        return@post
                    }
                    
                    // Crear álbum con imagen
                    val album = service.createAlbum(title, year, artistId!!, imageBytes!!, genre)
                    call.respond(HttpStatusCode.Created, album)
                    
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse(
                        error = "Internal Server Error",
                        message = "Error al crear álbum: ${e.message}",
                        status = 500
                    ))
                }
            }
        }
        
        // PUT /albums/{id} - Actualizar álbum
        put("/{id}") {
            val id = call.parameters["id"]
            if (id.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                    error = "Bad Request",
                    message = "Invalid album ID",
                    status = 400
                ))
                return@put
            }
            
            val request = call.receive<AlbumUpdateRequest>()
            val updated = service.updateAlbum(id, request)
            if (updated) {
                call.respond(HttpStatusCode.OK, SuccessResponse(
                    message = "Album updated successfully"
                ))
            } else {
                call.respond(HttpStatusCode.NotFound, ErrorResponse(
                    error = "Not Found",
                    message = "Album with ID $id not found",
                    status = 404
                ))
            }
        }
        
        // DELETE /albums/{id} - Eliminar álbum
        delete("/{id}") {
            val id = call.parameters["id"]
            if (id.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                    error = "Bad Request",
                    message = "Invalid album ID",
                    status = 400
                ))
                return@delete
            }
            
            val deleted = service.deleteAlbum(id)
            if (deleted) {
                call.respond(HttpStatusCode.OK, SuccessResponse(
                    message = "Album deleted successfully"
                ))
            } else {
                call.respond(HttpStatusCode.NotFound, ErrorResponse(
                    error = "Not Found",
                    message = "Album with ID $id not found",
                    status = 404
                ))
            }
        }
    }
    
    // GET /artists/{artistId}/albums - Obtener álbumes de un artista
    route("/artists/{artistId}/albums") {
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
            
            val albums = service.getAlbumsByArtist(artistId)
            if (albums == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                    error = "Bad Request",
                    message = "Invalid artist ID",
                    status = 400
                ))
            } else {
                call.respond(HttpStatusCode.OK, albums)
            }
        }
    }
}
