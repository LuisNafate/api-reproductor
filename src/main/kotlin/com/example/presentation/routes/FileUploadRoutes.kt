package com.example.presentation.routes

import com.example.presentation.dto.*
import com.example.service.S3Service
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.util.*

fun Route.fileUploadRoutes(s3Service: S3Service) {
    route("/api/v1/upload") {
        
        authenticate("auth-jwt") {
            /**
             * POST /api/v1/upload/image
             * Subir imagen vía multipart/form-data
             * 
             * Form fields:
             * - file: archivo de imagen (required)
             * - folder: carpeta destino en S3 (optional, default: "uploads")
             */
            post("/image") {
                val principal = call.principal<JWTPrincipal>()
                val username = principal?.payload?.getClaim("username")?.asString() ?: "anonymous"
                
                val multipart = call.receiveMultipart()
                var fileName = ""
                var fileBytes: ByteArray? = null
                var contentType = "image/jpeg"
                var folder = "uploads"
                
                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            if (part.name == "folder") {
                                folder = part.value
                            }
                        }
                        is PartData.FileItem -> {
                            if (part.name == "file") {
                                fileName = part.originalFileName ?: "image-${System.currentTimeMillis()}"
                                contentType = part.contentType?.toString() ?: "image/jpeg"
                                fileBytes = part.streamProvider().readBytes()
                            }
                        }
                        else -> {}
                    }
                    part.dispose()
                }
                
                if (fileBytes == null) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                        error = "Bad Request",
                        message = "No file provided",
                        status = 400
                    ))
                    return@post
                }
                
                // Validar tipo de archivo
                if (!contentType.startsWith("image/")) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                        error = "Bad Request",
                        message = "Only image files are allowed",
                        status = 400
                    ))
                    return@post
                }
                
                // Validar tamaño (máximo 10MB)
                if (fileBytes!!.size > 10 * 1024 * 1024) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                        error = "Bad Request",
                        message = "File size exceeds 10MB limit",
                        status = 400
                    ))
                    return@post
                }
                
                // Generar key único
                val extension = fileName.substringAfterLast(".", "jpg")
                val uniqueFileName = "${UUID.randomUUID()}-${System.currentTimeMillis()}.$extension"
                val s3Key = "$folder/$uniqueFileName"
                
                try {
                    // Subir a S3
                    val s3Key = s3Service.uploadFile(uniqueFileName, fileBytes!!, contentType)
                    
                    // Generar URL pre-firmada para acceso
                    val presignedUrl = s3Service.getPresignedUrl(s3Key)
                    
                    // Construir URL completa (aunque ahora usamos Keys)
                    val fileUrl = "https://${System.getenv("AWS_BUCKET") ?: "music-player-bucket"}.s3.${System.getenv("AWS_REGION") ?: "us-east-1"}.amazonaws.com/$s3Key"
                    
                    call.respond(HttpStatusCode.Created, FileUploadResponse(
                        fileName = uniqueFileName,
                        fileUrl = fileUrl,
                        presignedUrl = presignedUrl,
                        sizeBytes = fileBytes!!.size.toLong(),
                        contentType = contentType
                    ))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse(
                        error = "Internal Server Error",
                        message = "Failed to upload file: ${e.message}",
                        status = 500
                    ))
                }
            }
            
            /**
             * POST /api/v1/upload/presigned-url
             * Generar URL pre-firmada para subida directa desde cliente
             */
            post("/presigned-url") {
                try {
                    val request = call.receive<PresignedUrlRequest>()
                    
                    // Generar key único
                    val extension = request.fileName.substringAfterLast(".", "jpg")
                    val uniqueFileName = "${UUID.randomUUID()}-${System.currentTimeMillis()}.$extension"
                    val s3Key = "${request.folder}/$uniqueFileName"
                    
                    // Generar URL pre-firmada para PUT
                    val uploadUrl = s3Service.generatePresignedPutUrl(s3Key, request.contentType)
                    
                    call.respond(HttpStatusCode.OK, PresignedUrlResponse(
                        uploadUrl = uploadUrl,
                        fileKey = s3Key,
                        expiresInSeconds = 3600
                    ))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse(
                        error = "Internal Server Error",
                        message = "Failed to generate presigned URL: ${e.message}",
                        status = 500
                    ))
                }
            }
            
            /**
             * DELETE /api/v1/upload/{key}
             * Eliminar archivo de S3
             * Requiere rol ADMIN
             */
            delete("/{key...}") {
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString()
                
                if (role != "ADMIN") {
                    call.respond(HttpStatusCode.Forbidden, ErrorResponse(
                        error = "Forbidden",
                        message = "Solo administradores pueden eliminar archivos",
                        status = 403
                    ))
                    return@delete
                }
                
                val key = call.parameters.getAll("key")?.joinToString("/")
                
                if (key.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(
                        error = "Bad Request",
                        message = "File key is required",
                        status = 400
                    ))
                    return@delete
                }
                
                try {
                    s3Service.deleteFile(key)
                    call.respond(HttpStatusCode.OK, SuccessResponse(
                        message = "File deleted successfully"
                    ))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse(
                        error = "Internal Server Error",
                        message = "Failed to delete file: ${e.message}",
                        status = 500
                    ))
                }
            }
        }
    }
}
