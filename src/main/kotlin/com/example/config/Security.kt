package com.example.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {
    val jwtSecret = EnvLoader.get("JWT_SECRET", "default-secret-change-me")
    val jwtIssuer = EnvLoader.get("JWT_ISSUER", "http://0.0.0.0:8080")
    val jwtAudience = EnvLoader.get("JWT_AUDIENCE", "http://0.0.0.0:8080/auth")
    val jwtRealm = EnvLoader.get("JWT_REALM", "Access to 'auth'")

    authentication {
        jwt("auth-jwt") {
            realm = jwtRealm ?: "Access to 'auth'"
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}
