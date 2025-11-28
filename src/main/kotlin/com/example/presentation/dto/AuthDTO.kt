package com.example.presentation.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

/**
 * DTOs para autenticación
 */

data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String
)

data class AuthResponse(
    val token: String,
    @JsonProperty("user")
    val user: UserResponse
)

data class UserResponse(
    val id: String,
    val username: String,
    val email: String,
    val role: String
)
