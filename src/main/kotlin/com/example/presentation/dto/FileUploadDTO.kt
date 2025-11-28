package com.example.presentation.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Response al subir un archivo
 */
data class FileUploadResponse(
    @JsonProperty("file_name")
    val fileName: String,
    
    @JsonProperty("file_url")
    val fileUrl: String,
    
    @JsonProperty("presigned_url")
    val presignedUrl: String? = null,
    
    @JsonProperty("size_bytes")
    val sizeBytes: Long? = null,
    
    @JsonProperty("content_type")
    val contentType: String? = null
)

/**
 * Request para generar URL pre-firmada
 */
data class PresignedUrlRequest(
    @JsonProperty("file_name")
    val fileName: String,
    
    @JsonProperty("content_type")
    val contentType: String = "image/jpeg",
    
    @JsonProperty("folder")
    val folder: String = "uploads" // artists, albums, songs, playlists
)

/**
 * Response con URL pre-firmada para upload directo
 */
data class PresignedUrlResponse(
    @JsonProperty("upload_url")
    val uploadUrl: String,
    
    @JsonProperty("file_key")
    val fileKey: String,
    
    @JsonProperty("expires_in_seconds")
    val expiresInSeconds: Int = 3600 // 1 hora
)
