package com.example.presentation.routes

import com.example.presentation.dto.*
import com.example.service.ArtistService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Rutas REST para Artists
 */
fun Route.artistRoutes(service: ArtistService) {
    route("/artists") {
        // GET /artists - Obtener todos los artistas (público)
        get {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 100
            val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0
            val artists = service.getAllArtists(limit, offset)
            call.respond(HttpStatusCode.OK, artists)
        }
        
        // GET /artists/search?name=... - Buscar artistas por nombre (público)
        get("/search") {
            val name = call.request.queryParameters["name"]
            if (name.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                    error = "Bad Request",
                    message = "Query parameter 'name' is required",
                    status = 400
                ))
                return@get
            }
            val artists = service.searchArtists(name)
            call.respond(HttpStatusCode.OK, artists)
        }
        
        // GET /artists/{id} - Obtener artista por ID (público)
        get("/{id}") {
            val id = call.parameters["id"]
            if (id.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                    error = "Bad Request",
                    message = "Invalid artist ID",
                    status = 400
                ))
                return@get
            }
            
            val artist = service.getArtistById(id)
            if (artist == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse(
                    error = "Not Found",
                    message = "Artist with ID $id not found",
                    status = 404
                ))
            } else {
                call.respond(HttpStatusCode.OK, artist)
            }
        }
        
        // Rutas protegidas con autenticación
        authenticate("auth-jwt") {
            // POST /artists - Crear nuevo artista (requiere autenticación)
            post {
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString()
                
                // Solo ADMIN puede crear artistas
                if (role != "ADMIN") {
                    call.respond(HttpStatusCode.Forbidden, ErrorResponse(
                        error = "Forbidden",
                        message = "Solo administradores pueden crear artistas",
                        status = 403
                    ))
                    return@post
                }
                
                try {
                    // Procesar multipart/form-data
                    val multipart = call.receiveMultipart()
                    var name = ""
                    var biography: String? = null
                    var country: String? = null
                    var imageBytes: ByteArray? = null
                    
                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FormItem -> {
                                when (part.name) {
                                    "name" -> name = part.value
                                    "biography" -> biography = part.value
                                    "country" -> country = part.value
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
                    if (name.isEmpty()) {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                            error = "Bad Request",
                            message = "El campo 'name' es obligatorio",
                            status = 400
                        ))
                        return@post
                    }
                    
                    if (imageBytes == null) {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                            error = "Bad Request",
                            message = "Debe proporcionar una imagen",
                            status = 400
                        ))
                        return@post
                    }
                    
                    // Crear artista con imagen
                    val artist = service.createArtist(name, biography, country, imageBytes!!)
                    call.respond(HttpStatusCode.Created, artist)
                    
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse(
                        error = "Internal Server Error",
                        message = "Error al crear artista: ${e.message}",
                        status = 500
                    ))
                }
            }
            
            // PUT /artists/{id} - Actualizar artista (requiere autenticación)
            put("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString()
                
                // Solo ADMIN puede actualizar artistas
                if (role != "ADMIN") {
                    call.respond(HttpStatusCode.Forbidden, ErrorResponse(
                        error = "Forbidden",
                        message = "Solo administradores pueden actualizar artistas",
                        status = 403
                    ))
                    return@put
                }
                
                val id = call.parameters["id"]
                if (id.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                        error = "Bad Request",
                        message = "Invalid artist ID",
                        status = 400
                    ))
                    return@put
                }
                
                val request = call.receive<ArtistUpdateRequest>()
                val updated = service.updateArtist(id, request)
                if (updated) {
                    call.respond(HttpStatusCode.OK, SuccessResponse(
                        message = "Artist updated successfully"
                    ))
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse(
                        error = "Not Found",
                        message = "Artist with ID $id not found",
                        status = 404
                    ))
                }
            }
            
            // DELETE /artists/{id} - Eliminar artista (requiere autenticación)
            delete("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString()
                
                // Solo ADMIN puede eliminar artistas
                if (role != "ADMIN") {
                    call.respond(HttpStatusCode.Forbidden, ErrorResponse(
                        error = "Forbidden",
                        message = "Solo administradores pueden eliminar artistas",
                        status = 403
                    ))
                    return@delete
                }
                
                val id = call.parameters["id"]
                if (id.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                        error = "Bad Request",
                        message = "Invalid artist ID",
                        status = 400
                    ))
                    return@delete
                }
                
                val deleted = service.deleteArtist(id)
                if (deleted) {
                    call.respond(HttpStatusCode.OK, SuccessResponse(
                        message = "Artist deleted successfully"
                    ))
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse(
                        error = "Not Found",
                        message = "Artist with ID $id not found",
                        status = 404
                    ))
                }
            }
        }
    }
}
