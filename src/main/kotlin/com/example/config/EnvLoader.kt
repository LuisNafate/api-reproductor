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
            envFile.readLines().forEach { line ->
                val trimmed = line.trim()
                // Ignorar líneas vacías y comentarios
                if (trimmed.isNotEmpty() && !trimmed.startsWith("#")) {
                    val parts = trimmed.split("=", limit = 2)
                    if (parts.size == 2) {
                        val key = parts[0].trim()
                        val value = parts[1].trim()
                        properties.setProperty(key, value)
                        if (key.startsWith("AWS_")) {
                            println("   📝 Cargada: $key = ${value.take(10)}...")
                        }
                    }
                }
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
        val fromProperties = properties.getProperty(key)
        val fromSystem = System.getenv(key)
        val result = fromProperties ?: fromSystem ?: default
        
        if (key.startsWith("AWS_")) {
            println("  🔑 EnvLoader.get('$key'):")
            println("     - From .env: ${fromProperties?.take(10) ?: "null"}")
            println("     - From system: ${fromSystem?.take(10) ?: "null"}")
            println("     - Default: ${default?.take(10) ?: "null"}")
            println("     - Result: ${result?.take(10) ?: "null"}")
        }
        
        return result
    }
    
    /**
     * Obtiene una variable de entorno como String, lanza excepción si no existe
     */
    fun getRequired(key: String): String {
        return get(key) ?: throw IllegalStateException("Required environment variable $key not found")
    }
}
