package com.example.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.config.EnvLoader
import com.example.data.database.UsersTable
import com.example.domain.models.User
import com.example.presentation.dto.AuthResponse
import com.example.presentation.dto.LoginRequest
import com.example.presentation.dto.RegisterRequest
import com.example.presentation.dto.UserResponse
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.*

class AuthService {
    
    private val jwtSecret = EnvLoader.get("JWT_SECRET", "default-secret-change-me")
    private val jwtIssuer = EnvLoader.get("JWT_ISSUER", "http://0.0.0.0:8080")
    private val jwtAudience = EnvLoader.get("JWT_AUDIENCE", "http://0.0.0.0:8080/auth")
    private val jwtRealm = EnvLoader.get("JWT_REALM", "Access to 'auth'")
    
    /**
     * Registrar un nuevo usuario
     */
    suspend fun register(request: RegisterRequest): Result<AuthResponse> = transaction {
        try {
            // Verificar si el usuario ya existe
            val existingUser = UsersTable.selectAll()
                .where { (UsersTable.username eq request.username) or (UsersTable.email eq request.email) }
                .singleOrNull()
            
            if (existingUser != null) {
                return@transaction Result.failure(Exception("El usuario o email ya existe"))
            }
            
            // Hash de la contraseña
            val hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())
            
            // Insertar usuario
            val userId = UUID.randomUUID()
            UsersTable.insert {
                it[UsersTable.id] = userId
                it[UsersTable.username] = request.username
                it[UsersTable.password] = hashedPassword
                it[UsersTable.email] = request.email
                it[UsersTable.role] = "USER"
            }
            
            // Generar token
            val token = generateToken(request.username, "USER")
            
            val userResponse = UserResponse(
                id = userId.toString(),
                username = request.username,
                email = request.email,
                role = "USER"
            )
            
            Result.success(AuthResponse(token = token, user = userResponse))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Login de usuario
     */
    suspend fun login(request: LoginRequest): Result<AuthResponse> = transaction {
        try {
            // Buscar usuario por username
            val userRow = UsersTable.selectAll()
                .where { UsersTable.username eq request.username }
                .singleOrNull()
                ?: return@transaction Result.failure(Exception("Credenciales inválidas"))
            
            // Verificar contraseña
            val storedPassword = userRow[UsersTable.password]
            if (!BCrypt.checkpw(request.password, storedPassword)) {
                return@transaction Result.failure(Exception("Credenciales inválidas"))
            }
            
            // Generar token
            val username = userRow[UsersTable.username]
            val role = userRow[UsersTable.role]
            val token = generateToken(username, role)
            
            val userResponse = UserResponse(
                id = userRow[UsersTable.id].value.toString(),
                username = username,
                email = userRow[UsersTable.email],
                role = role
            )
            
            Result.success(AuthResponse(token = token, user = userResponse))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Generar JWT token
     */
    private fun generateToken(username: String, role: String): String {
        return JWT.create()
            .withAudience(jwtAudience)
            .withIssuer(jwtIssuer)
            .withClaim("username", username)
            .withClaim("role", role)
            .withExpiresAt(Date(System.currentTimeMillis() + 86400000)) // 24 horas
            .sign(Algorithm.HMAC256(jwtSecret))
    }
    
    /**
     * Obtener usuario por username
     */
    suspend fun getUserByUsername(username: String): User? = transaction {
        UsersTable.selectAll()
            .where { UsersTable.username eq username }
            .singleOrNull()?.let {
                User(
                    id = it[UsersTable.id].value,
                    username = it[UsersTable.username],
                    email = it[UsersTable.email],
                    role = it[UsersTable.role],
                    createdAt = it[UsersTable.createdAt],
                    updatedAt = it[UsersTable.updatedAt]
                )
            }
    }
}
