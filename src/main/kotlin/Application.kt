package com.example

import com.example.config.DatabaseFactory
import com.example.config.configureCORS
import com.example.config.configureLogging
import com.example.config.configureStatusPages
import com.example.config.configureSecurity
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // Inicializar base de datos
    DatabaseFactory.init()
    
    // Configurar plugins
    configureLogging()
    configureCORS()
    configureStatusPages()
    configureSerialization()
    configureSecurity()
    
    // Configurar rutas
    configureRouting()
}
