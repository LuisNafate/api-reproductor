package com.example.config

import com.example.presentation.dto.ErrorResponse
import com.example.util.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

/**
 * Configuración del manejo de errores y excepciones
 */
fun Application.configureStatusPages() {
    install(StatusPages) {
        // Manejo de excepciones personalizadas
        exception<NotFoundException> { call, cause ->
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(
                    error = "Not Found",
                    message = cause.message ?: "Resource not found",
                    status = 404
                )
            )
        }
        
        exception<BadRequestException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    error = "Bad Request",
                    message = cause.message ?: "Invalid request",
                    status = 400
                )
            )
        }
        
        exception<ConflictException> { call, cause ->
            call.respond(
                HttpStatusCode.Conflict,
                ErrorResponse(
                    error = "Conflict",
                    message = cause.message ?: "Resource conflict",
                    status = 409
                )
            )
        }
        
        exception<UnauthorizedException> { call, cause ->
            call.respond(
                HttpStatusCode.Unauthorized,
                ErrorResponse(
                    error = "Unauthorized",
                    message = cause.message ?: "Authentication required",
                    status = 401
                )
            )
        }
        
        exception<ForbiddenException> { call, cause ->
            call.respond(
                HttpStatusCode.Forbidden,
                ErrorResponse(
                    error = "Forbidden",
                    message = cause.message ?: "Access denied",
                    status = 403
                )
            )
        }
        
        // Manejo de errores de serialización JSON
        exception<kotlinx.serialization.SerializationException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    error = "Bad Request",
                    message = "Invalid JSON format: ${cause.message}",
                    status = 400
                )
            )
        }
        
        // Manejo de errores genéricos
        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Unhandled exception", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    error = "Internal Server Error",
                    message = "An unexpected error occurred",
                    status = 500
                )
            )
        }
        
        // Manejo de estados HTTP personalizados
        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(
                status,
                ErrorResponse(
                    error = "Not Found",
                    message = "The requested resource was not found",
                    status = status.value
                )
            )
        }
    }
}
