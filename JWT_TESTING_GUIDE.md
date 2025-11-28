# 🎵 Music Player API - JWT Authentication

API REST con autenticación JWT implementada en Ktor + PostgreSQL + Exposed ORM.

## 🚀 Características Implementadas

### ✅ Autenticación JWT
- ✅ Registro de usuarios (`POST /api/v1/auth/register`)
- ✅ Login con generación de token JWT (`POST /api/v1/auth/login`)
- ✅ Token válido por 24 horas
- ✅ Password hashing con BCrypt
- ✅ Roles de usuario (USER, ADMIN)

### 🔒 Protección de Rutas
- ✅ Endpoints GET públicos (lectura abierta)
- ✅ Endpoints POST/PUT/DELETE protegidos con JWT
- ✅ Validación de roles (solo ADMIN puede crear/editar/eliminar)
- ✅ Response 401 Unauthorized si no hay token
- ✅ Response 403 Forbidden si no es ADMIN

### 🗄️ Base de Datos
- ✅ PostgreSQL con HikariCP
- ✅ Tabla `users` con UUID, username único, email único, role
- ✅ Tablas: artists, albums, songs, playlists, playlist_songs

## 📋 Testing en Postman

### 1. Importar la Colección

Importa el archivo: `Music_Player_API_JWT.postman_collection.json`

### 2. Flujo de Prueba

#### Paso 1: Registrar Usuario
```
POST http://127.0.0.1:8080/api/v1/auth/register

Body (JSON):
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "test123"
}

Response 201:
{
  "token": "eyJ0eXAiOiJKV1QiLCJhbGc...",
  "user": {
    "id": "uuid-here",
    "username": "testuser",
    "email": "test@example.com",
    "role": "USER"
  }
}
```

#### Paso 2: Login
```
POST http://127.0.0.1:8080/api/v1/auth/login

Body (JSON):
{
  "username": "testuser",
  "password": "test123"
}

Response 200:
{
  "token": "eyJ0eXAiOiJKV1QiLCJhbGc...",
  "user": { ... }
}
```

⚠️ **El script en Postman guarda automáticamente el token en la variable `jwt_token`**

#### Paso 3: Probar Endpoint Público (GET Artists)
```
GET http://127.0.0.1:8080/api/v1/artists

No requiere autenticación ✅
```

#### Paso 4: Probar Endpoint Protegido (Create Artist)
```
POST http://127.0.0.1:8080/api/v1/artists
Authorization: Bearer {{jwt_token}}

Body (JSON):
{
  "name": "Test Artist",
  "bio": "Test bio",
  "imageUrl": "https://example.com/image.jpg"
}

Con rol USER: Response 403 Forbidden ❌
Con rol ADMIN: Response 201 Created ✅
```

### 3. Crear Usuario ADMIN

Para probar endpoints protegidos, necesitas un usuario con rol ADMIN:

**Opción A: Vía SQL (Recomendado)**
```sql
-- Ejecutar en pgAdmin o psql
UPDATE users SET role = 'ADMIN' WHERE username = 'testuser';
```

**Opción B: Crear superadmin con script**
```bash
psql -U music_admin -d music_player -f create_admin_user.sql
```

Luego haz login nuevamente para obtener un token con rol ADMIN.

## 🔧 Configuración JWT

Variables en `.env`:
```env
JWT_SECRET=my-secret-key-change-in-production-please-use-strong-secret
JWT_ISSUER=http://0.0.0.0:8080
JWT_AUDIENCE=http://0.0.0.0:8080/auth
JWT_REALM=Access to 'auth'
```

⚠️ **En producción, usa un secret fuerte (mínimo 32 caracteres aleatorios)**

## 📊 Estructura del Token JWT

```json
{
  "aud": "http://0.0.0.0:8080/auth",
  "iss": "http://0.0.0.0:8080",
  "username": "testuser",
  "role": "ADMIN",
  "exp": 1732905600000
}
```

## 🛡️ Seguridad Implementada

1. **Password Hashing**: BCrypt con salt automático
2. **Token Expiration**: 24 horas desde generación
3. **Role-Based Access Control (RBAC)**: 
   - USER: Solo lectura
   - ADMIN: Lectura + Escritura
4. **Unique Constraints**: username y email únicos en DB
5. **Validation**: JWT verifier con Algorithm.HMAC256

## 📝 Endpoints Disponibles

### Autenticación (Público)
- `POST /api/v1/auth/register` - Registrar usuario
- `POST /api/v1/auth/login` - Login

### Artists
- `GET /api/v1/artists` - Listar (público)
- `GET /api/v1/artists/{id}` - Obtener por ID (público)
- `GET /api/v1/artists/search?name=...` - Buscar (público)
- `POST /api/v1/artists` - Crear (🔒 ADMIN)
- `PUT /api/v1/artists/{id}` - Actualizar (🔒 ADMIN)
- `DELETE /api/v1/artists/{id}` - Eliminar (🔒 ADMIN)

### Albums, Songs, Playlists
Misma lógica: GET público, POST/PUT/DELETE protegido

## 🐛 Troubleshooting

### Token inválido
```
Response 401: {"error": "Unauthorized"}
```
- Verifica que el token no haya expirado (24h)
- Asegúrate de incluir `Authorization: Bearer TOKEN`
- El token debe empezar con `eyJ...`

### Forbidden
```
Response 403: {"error": "Solo administradores pueden crear artistas"}
```
- Tu usuario tiene rol USER, necesitas ADMIN
- Ejecuta: `UPDATE users SET role = 'ADMIN' WHERE username = 'tu_usuario';`

### Usuario ya existe
```
Response 400: {"error": "El usuario o email ya existe"}
```
- El username o email ya están registrados
- Usa credenciales diferentes

## 🚀 Próximos Pasos

1. ✅ JWT Authentication (COMPLETADO)
2. ✅ AWS S3 Integration (COMPLETADO) - Ver `AWS_S3_GUIDE.md`
3. ⏳ UUID Migration (pendiente)

## 📸 Subir Imágenes con AWS S3

### Configuración Rápida

1. **Actualiza `.env` con tus credenciales AWS:**
```env
AWS_ACCESS_KEY=tu-access-key
AWS_SECRET_KEY=tu-secret-key
AWS_BUCKET=music-player-bucket
AWS_REGION=us-east-1
```

2. **Crea el bucket en AWS S3:**
```bash
aws s3 mb s3://music-player-bucket --region us-east-1
```

### Flujo: Crear Artista con Imagen

**1. Subir imagen a S3:**
```http
POST http://127.0.0.1:8080/api/v1/upload/image
Authorization: Bearer {{jwt_token}}
Content-Type: multipart/form-data

Form Data:
- file: [tu-imagen.jpg]
- folder: artists
```

**Response:**
```json
{
  "file_name": "abc123-1234567890.jpg",
  "file_url": "https://music-player-bucket.s3.us-east-1.amazonaws.com/artists/abc123-1234567890.jpg",
  "presigned_url": "https://...?X-Amz-Algorithm=...",
  "size_bytes": 245678,
  "content_type": "image/jpeg"
}
```

**2. Crear artista con la URL:**
```http
POST http://127.0.0.1:8080/api/v1/artists
Authorization: Bearer {{jwt_token}}

{
  "name": "Taylor Swift",
  "bio": "Singer-songwriter",
  "image_url": "https://music-player-bucket.s3.us-east-1.amazonaws.com/artists/abc123-1234567890.jpg"
}
```

**3. Consultar artista (incluye URL pre-firmada):**
```http
GET http://127.0.0.1:8080/api/v1/artists/1

Response:
{
  "id": 1,
  "name": "Taylor Swift",
  "image_url": "https://music-player-bucket.s3.us-east-1.amazonaws.com/artists/abc123-1234567890.jpg",
  "image_presigned_url": "https://...?X-Amz-Algorithm=..." // Válida 24h
}
```

📖 **Guía completa:** `AWS_S3_GUIDE.md`

---

## 🚀 Próximos Pasos (Actualizado)

1. ✅ JWT Authentication (COMPLETADO)
2. ⏳ AWS S3 Integration (pendiente)
3. ⏳ Image Upload (pendiente)
4. ⏳ UUID Migration (pendiente)

## 📦 Dependencias

```kotlin
// JWT & Auth
implementation("io.ktor:ktor-server-auth")
implementation("io.ktor:ktor-server-auth-jwt")
implementation("org.mindrot:jbcrypt:0.4")

// Database
implementation("org.postgresql:postgresql:42.7.3")
implementation("org.jetbrains.exposed:exposed-*:0.55.0")
```

## 🔗 Referencias

- [Repositorio de referencia](https://github.com/luvips/API-KTOR-spotify)
- [Ktor JWT Documentation](https://ktor.io/docs/jwt.html)
- [Exposed ORM](https://github.com/JetBrains/Exposed)
