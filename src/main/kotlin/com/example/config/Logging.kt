package com.example.config

import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.request.*
import org.slf4j.event.Level

/**
 * Configuración del logging de peticiones HTTP
 */
fun Application.configureLogging() {
    install(CallLogging) {
        level = Level.INFO
        
        // Filtrar información sensible
        filter { call -> call.request.path().startsWith("/") }
        
        // Formato del log
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val uri = call.request.uri
            val duration = call.processingTimeMillis()
            "$httpMethod $uri - $status (${duration}ms)"
        }
    }
}
