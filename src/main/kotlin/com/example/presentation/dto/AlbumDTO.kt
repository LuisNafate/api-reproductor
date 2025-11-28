package com.example.presentation.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * DTOs para Album
 */
data class AlbumRequest(
    val title: String,
    @JsonProperty("artist_id") val artistId: String,
    @JsonProperty("release_date") val releaseDate: LocalDate? = null,
    @JsonProperty("cover_image_url") val coverImageUrl: String? = null,
    val genre: String? = null
)

data class AlbumResponse(
    val id: String,
    val title: String,
    @JsonProperty("artist_id") val artistId: String,
    @JsonProperty("release_date") val releaseDate: LocalDate?,
    @JsonProperty("cover_image_url") val coverImageUrl: String?,
    @JsonProperty("cover_presigned_url") val coverPresignedUrl: String? = null, // URL pre-firmada
    val genre: String?,
    @JsonProperty("created_at") val createdAt: LocalDateTime,
    @JsonProperty("updated_at") val updatedAt: LocalDateTime
)

data class AlbumUpdateRequest(
    val title: String? = null,
    @JsonProperty("artist_id") val artistId: String? = null,
    @JsonProperty("release_date") val releaseDate: LocalDate? = null,
    @JsonProperty("cover_image_url") val coverImageUrl: String? = null,
    val genre: String? = null
)
