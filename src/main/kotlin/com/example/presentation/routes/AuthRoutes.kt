package com.example.presentation.routes

import com.example.presentation.dto.LoginRequest
import com.example.presentation.dto.RegisterRequest
import com.example.service.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {
        
        /**
         * POST /auth/register
         * Registrar nuevo usuario
         */
        post("/register") {
            val request = call.receive<RegisterRequest>()
            
            val result = authService.register(request)
            
            result.fold(
                onSuccess = { authResponse ->
                    call.respond(HttpStatusCode.Created, authResponse)
                },
                onFailure = { error ->
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to error.message))
                }
            )
        }
        
        /**
         * POST /auth/login
         * Login de usuario
         */
        post("/login") {
            val request = call.receive<LoginRequest>()
            
            val result = authService.login(request)
            
            result.fold(
                onSuccess = { authResponse ->
                    call.respond(HttpStatusCode.OK, authResponse)
                },
                onFailure = { error ->
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to error.message))
                }
            )
        }
    }
}
