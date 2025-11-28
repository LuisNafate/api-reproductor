package com.example.service

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.*
import aws.sdk.kotlin.services.s3.presigners.presignGetObject
import aws.sdk.kotlin.services.s3.presigners.presignPutObject
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.net.url.Url
import com.example.config.EnvLoader
import java.util.UUID
import kotlin.time.Duration.Companion.hours

class S3Service {
    
    private val bucketName = EnvLoader.get("AWS_BUCKET", "music-player-bucket")
    private val region = EnvLoader.get("AWS_REGION", "us-east-1")
    private val accessKey = EnvLoader.get("AWS_ACCESS_KEY", "")
    private val secretKey = EnvLoader.get("AWS_SECRET_KEY", "")
    
    private val s3Client = S3Client {
        region = this@S3Service.region
        credentialsProvider = StaticCredentialsProvider {
            accessKeyId = this@S3Service.accessKey
            secretAccessKey = this@S3Service.secretKey
        }
    }
    
    /**
     * Subir archivo a S3
     * @param fileName Nombre base del archivo
     * @param fileBytes Contenido del archivo
     * @param contentType Tipo MIME (ej: "image/jpeg")
     * @return KEY del archivo (solo el nombre único, no URL)
     */
    suspend fun uploadFile(fileName: String, fileBytes: ByteArray, contentType: String): String {
        // Generar nombre único con UUID
        val uniqueFileName = "${UUID.randomUUID()}-$fileName"
        
        val putObjectRequest = PutObjectRequest {
            bucket = bucketName
            this.key = uniqueFileName
            this.contentType = contentType
            body = ByteStream.fromBytes(fileBytes)
        }
        
        s3Client.putObject(putObjectRequest)
        
        // Retornar solo la KEY (nombre del archivo), NO la URL
        return uniqueFileName
    }
    
    /**
     * Generar URL pre-firmada para GET (descarga/visualización)
     * Válida por 12 horas
     * @param key Nombre/path del archivo en S3
     * @return URL pre-firmada
     */
    suspend fun getPresignedUrl(key: String): String {
        val getObjectRequest = GetObjectRequest {
            bucket = bucketName
            this.key = key
        }
        
        val presignedRequest = s3Client.presignGetObject(getObjectRequest, 12.hours)
        return presignedRequest.url.toString()
    }
    
    /**
     * Generar URL pre-firmada para PUT (subida directa desde cliente)
     * Válida por 1 hora
     * @param key Nombre/path del archivo en S3
     * @param contentType Tipo MIME
     * @return URL pre-firmada para upload
     */
    suspend fun generatePresignedPutUrl(key: String, contentType: String): String {
        val putObjectRequest = PutObjectRequest {
            bucket = bucketName
            this.key = key
            this.contentType = contentType
        }
        
        val presignedRequest = s3Client.presignPutObject(putObjectRequest, 1.hours)
        return presignedRequest.url.toString()
    }
    
    /**
     * Eliminar archivo de S3
     * @param key Nombre/path del archivo en S3
     */
    suspend fun deleteFile(key: String) {
        val deleteObjectRequest = DeleteObjectRequest {
            bucket = bucketName
            this.key = key
        }
        
        s3Client.deleteObject(deleteObjectRequest)
    }
    
    /**
     * Verificar si un archivo existe en S3
     * @param key Nombre/path del archivo
     * @return true si existe, false si no
     */
    suspend fun fileExists(key: String): Boolean {
        return try {
            val headObjectRequest = HeadObjectRequest {
                bucket = bucketName
                this.key = key
            }
            s3Client.headObject(headObjectRequest)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Extraer el key (nombre del archivo) desde una URL de S3
     * Ejemplo: "https://bucket.s3.region.amazonaws.com/artists/image.jpg" -> "artists/image.jpg"
     */
    fun extractKeyFromUrl(url: String): String? {
        return try {
            val urlObj = Url.parse(url)
            urlObj.path.segments.drop(1).joinToString("/") // Quitar el primer '/'
        } catch (e: Exception) {
            null
        }
    }
}
