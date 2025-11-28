package com.example.presentation.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

/**
 * DTOs para Playlist
 */
data class PlaylistRequest(
    val name: String,
    val description: String? = null,
    @JsonProperty("user_id") val userId: String,
    @JsonProperty("is_public") val isPublic: Boolean = true,
    @JsonProperty("cover_image_url") val coverImageUrl: String? = null
)

data class PlaylistResponse(
    val id: String,
    val name: String,
    val description: String?,
    @JsonProperty("user_id") val userId: String,
    @JsonProperty("is_public") val isPublic: Boolean,
    @JsonProperty("cover_image_url") val coverImageUrl: String?,
    @JsonProperty("created_at") val createdAt: LocalDateTime,
    @JsonProperty("updated_at") val updatedAt: LocalDateTime
)

data class PlaylistUpdateRequest(
    val name: String? = null,
    val description: String? = null,
    @JsonProperty("is_public") val isPublic: Boolean? = null,
    @JsonProperty("cover_image_url") val coverImageUrl: String? = null
)

data class AddSongToPlaylistRequest(
    @JsonProperty("song_id") val songId: String,
    val position: Int? = null
)
