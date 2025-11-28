package com.example.presentation.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

/**
 * DTOs para Artist
 */
data class ArtistRequest(
    val name: String,
    val biography: String? = null,
    val country: String? = null,
    @JsonProperty("image_url") val imageUrl: String? = null
)

data class ArtistResponse(
    val id: String,
    val name: String,
    val biography: String?,
    val country: String?,
    @JsonProperty("image_url") val imageUrl: String?,
    @JsonProperty("image_presigned_url") val imagePresignedUrl: String? = null, // URL pre-firmada para visualización
    @JsonProperty("created_at") val createdAt: LocalDateTime,
    @JsonProperty("updated_at") val updatedAt: LocalDateTime
)

data class ArtistUpdateRequest(
    val name: String? = null,
    val biography: String? = null,
    val country: String? = null,
    @JsonProperty("image_url") val imageUrl: String? = null
)
