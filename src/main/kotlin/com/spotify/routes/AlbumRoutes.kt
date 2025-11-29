package com.spotify.routes

import com.spotify.services.AlbumService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.albumRoutes(albumService: AlbumService) {
    route("/albums") {

        get("/all") {
            try {
                val albums = albumService.getAll()
                call.respond(HttpStatusCode.OK, albums)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error al obtener álbumes: ${e.message}")
            }
        }

        get("/{id}") {
            val idParam = call.parameters["id"]
            if (idParam != null) {
                try {
                    val albumId = UUID.fromString(idParam)
                    val album = albumService.getById(albumId)
                    if (album != null) {
                        call.respond(HttpStatusCode.OK, album)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Álbum no encontrado")
                    }
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, "ID inválido")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Falta el ID del álbum")
            }
        }

        get("/artist/{id}") {
            val idParam = call.parameters["id"]
            if (idParam != null) {
                try {
                    val artistId = UUID.fromString(idParam)
                    val albums = albumService.getByArtistId(artistId)
                    call.respond(HttpStatusCode.OK, albums)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, "ID inválido")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Falta el ID del artista")
            }
        }

        authenticate("auth-jwt") {
            post {
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString()

                if (role != "ADMIN") {
                    call.respond(HttpStatusCode.Forbidden, "No tienes permisos de administrador")
                    return@post
                }

                try {
                    val multipart = call.receiveMultipart()
                    var name = ""
                    var year = 0
                    var artistId: UUID? = null
                    var imageBytes: ByteArray? = null

                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FormItem -> {
                                when (part.name) {
                                    "name" -> name = part.value
                                    "year" -> year = part.value.toIntOrNull() ?: 0
                                    "artistId" -> artistId = try { UUID.fromString(part.value) } catch (e: Exception) { null }
                                }
                            }
                            is PartData.FileItem -> {
                                if (part.name == "image") {
                                    imageBytes = part.streamProvider().readBytes()
                                }
                            }
                            else -> part.dispose()
                        }
                        part.dispose()
                    }

                    if (name.isNotEmpty() && year > 0 && artistId != null && imageBytes != null) {
                        val album = albumService.create(name, year, artistId!!, imageBytes!!)
                        call.respond(HttpStatusCode.Created, album)
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Faltan datos obligatorios: name, year, artistId o image")
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, "Error al procesar la subida: ${e.message}")
                }
            }

            put("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString()
                if (role != "ADMIN") {
                    call.respond(HttpStatusCode.Forbidden, "No tienes permisos de administrador")
                    return@put
                }

                val idParam = call.parameters["id"]
                if (idParam == null) {
                    call.respond(HttpStatusCode.BadRequest, "Falta el ID del álbum")
                    return@put
                }

                try {
                    val albumId = UUID.fromString(idParam)
                    val multipart = call.receiveMultipart()
                    var name: String? = null
                    var year: Int? = null
                    var artistId: UUID? = null
                    var imageBytes: ByteArray? = null

                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FormItem -> {
                                when (part.name) {
                                    "name" -> name = part.value
                                    "year" -> year = part.value.toIntOrNull()
                                    "artistId" -> artistId = try { UUID.fromString(part.value) } catch (e: Exception) { null }
                                }
                            }
                            is PartData.FileItem -> {
                                if (part.name == "image") {
                                    imageBytes = part.streamProvider().readBytes()
                                }
                            }
                            else -> part.dispose()
                        }
                        part.dispose()
                    }

                    val updatedAlbum = albumService.update(albumId, name, year, artistId, imageBytes)
                    if (updatedAlbum != null) {
                        call.respond(HttpStatusCode.OK, updatedAlbum)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Álbum no encontrado")
                    }

                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, "ID inválido")
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, "Error al actualizar álbum: ${e.message}")
                }
            }

            delete("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString()
                if (role != "ADMIN") {
                    call.respond(HttpStatusCode.Forbidden, "No tienes permisos de administrador")
                    return@delete
                }

                val idParam = call.parameters["id"]
                if (idParam == null) {
                    call.respond(HttpStatusCode.BadRequest, "Falta el ID del álbum")
                    return@delete
                }

                try {
                    val albumId = UUID.fromString(idParam)
                    val deleted = albumService.delete(albumId)
                    if (deleted) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Álbum eliminado exitosamente"))
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Álbum no encontrado")
                    }
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, "ID inválido")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error al eliminar álbum: ${e.message}")
                }
            }
        }
    }
}
