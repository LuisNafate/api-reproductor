package com.example.config

import java.io.File
import java.util.Properties

/**
 * Carga las variables de entorno desde el archivo .env
 */
object EnvLoader {
    
    private val properties = Properties()
    
    init {
        loadEnvFile()
    }
    
    private fun loadEnvFile() {
        val envFile = File(".env")
        if (envFile.exists()) {
            envFile.inputStream().use { stream ->
                properties.load(stream)
            }
            println("Loaded .env file successfully")
        } else {
            println("Warning: .env file not found, using system environment variables or defaults")
        }
    }
    
    /**
     * Obtiene una variable de entorno, primero del .env, luego del sistema, o retorna el default
     */
    fun get(key: String, default: String? = null): String? {
        return properties.getProperty(key) ?: System.getenv(key) ?: default
    }
    
    /**
     * Obtiene una variable de entorno como String, lanza excepción si no existe
     */
    fun getRequired(key: String): String {
        return get(key) ?: throw IllegalStateException("Required environment variable $key not found")
    }
}
