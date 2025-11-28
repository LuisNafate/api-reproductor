plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
}

group = "com.example"
version = "0.0.1"

val ktor_version="3.3.2"

application {
    mainClass = "com.example.ApplicationKt"
}

tasks {
    shadowJar {
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "com.example.ApplicationKt"))
        }
        archiveFileName.set("mi-api.jar")
    }
}

dependencies {
    // Ktor Core
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.jackson)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation("io.ktor:ktor-client-content-negotiation:3.3.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.3.2")
    implementation("io.ktor:ktor-serialization-jackson:$ktor_version")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
    
    // Ktor Status Pages (para manejo de errores)
    implementation("io.ktor:ktor-server-status-pages:$ktor_version")
    implementation("io.ktor:ktor-server-call-logging:$ktor_version")
    implementation("io.ktor:ktor-server-cors:$ktor_version")
    
    // Database - Exposed ORM
    implementation("org.jetbrains.exposed:exposed-core:0.55.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.55.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.55.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.55.0")
    
    // Database Drivers
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("com.h2database:h2:2.2.224") // Para testing
    
    // Connection Pool
    implementation("com.zaxxer:HikariCP:5.1.0")
    
    // Validation
    implementation("io.konform:konform-jvm:0.4.0")
    
    // BCrypt para password hashing
    implementation("org.mindrot:jbcrypt:0.4")
    
    // AWS S3 SDK
    implementation("aws.sdk.kotlin:s3:1.0.30")
    implementation("aws.smithy.kotlin:http-client-engine-okhttp:1.0.10")
    
    // Multipart file upload
    implementation("io.ktor:ktor-server-partial-content:$ktor_version")
    
    // Testing
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}
