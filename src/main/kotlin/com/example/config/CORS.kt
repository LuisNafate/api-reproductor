package com.example.config

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

/**
 * Configuración de CORS para permitir peticiones desde diferentes orígenes
 */
fun Application.configureCORS() {
    install(CORS) {
        // Permitir cualquier host en desarrollo
        anyHost()
        
        // Métodos HTTP permitidos
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Options)
        
        // Headers permitidos
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.Accept)
        
        // Exponer headers en la respuesta
        exposeHeader(HttpHeaders.ContentType)
        exposeHeader(HttpHeaders.Authorization)
        
        // Permitir credenciales
        allowCredentials = true
        
        // Tiempo de cache para preflight requests
        maxAgeInSeconds = 3600
    }
}
