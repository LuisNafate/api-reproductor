package com.example.presentation.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

/**
 * DTOs para Song
 */
data class SongRequest(
    val title: String,
    @JsonProperty("artist_id") val artistId: String,
    @JsonProperty("album_id") val albumId: String? = null,
    @JsonProperty("duration_seconds") val durationSeconds: Int,
    @JsonProperty("file_url") val fileUrl: String,
    val genre: String? = null
)

data class SongResponse(
    val id: String,
    val title: String,
    @JsonProperty("artist_id") val artistId: String,
    @JsonProperty("album_id") val albumId: String?,
    @JsonProperty("duration_seconds") val durationSeconds: Int,
    @JsonProperty("file_url") val fileUrl: String,
    val genre: String?,
    @JsonProperty("play_count") val playCount: Int,
    @JsonProperty("created_at") val createdAt: LocalDateTime,
    @JsonProperty("updated_at") val updatedAt: LocalDateTime
)

data class SongUpdateRequest(
    val title: String? = null,
    @JsonProperty("artist_id") val artistId: String? = null,
    @JsonProperty("album_id") val albumId: String? = null,
    @JsonProperty("duration_seconds") val durationSeconds: Int? = null,
    @JsonProperty("file_url") val fileUrl: String? = null,
    val genre: String? = null
)
