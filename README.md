# 🎵 Music Player API

API REST para reproductor de música construida con Kotlin y Ktor.

## 🚀 Inicio Rápido

### 1. Configurar PostgreSQL en pgAdmin

1. Crear usuario: `music_admin` con password: `music1234`
2. Crear base de datos: `music_player`
3. Asignar permisos al usuario

*Ver guía detallada en: `VER_TABLAS_POSTGRES.md`*

### 2. Iniciar el Servidor

```powershell
./gradlew run
```

El servidor estará disponible en: **http://localhost:8080**

## 📋 Endpoints Disponibles

### Base URL: `http://localhost:8080/api/v1`

### ❤️ Health Check
```
GET /health
```

### 🎤 Artistas
```
GET    /artists              # Listar (con paginación: ?limit=10&offset=0)
POST   /artists              # Crear
GET    /artists/{id}         # Obtener por ID
GET    /artists/search       # Buscar (?name=beatles)
PUT    /artists/{id}         # Actualizar
DELETE /artists/{id}         # Eliminar
```

### 💿 Álbumes
```
GET    /albums               # Listar
POST   /albums               # Crear
GET    /albums/{id}          # Obtener por ID
GET    /artists/{id}/albums  # Álbumes de un artista
PUT    /albums/{id}          # Actualizar
DELETE /albums/{id}          # Eliminar
```

### 🎵 Canciones
```
GET    /songs                # Listar
POST   /songs                # Crear
GET    /songs/{id}           # Obtener por ID
POST   /songs/{id}/play      # Reproducir (incrementa contador)
GET    /songs/search         # Buscar (?title=come)
GET    /artists/{id}/songs   # Canciones de un artista
GET    /albums/{id}/songs    # Canciones de un álbum
PUT    /songs/{id}           # Actualizar
DELETE /songs/{id}           # Eliminar
```

### 📝 Playlists
```
GET    /playlists            # Listar
POST   /playlists            # Crear
GET    /playlists/{id}       # Obtener por ID
GET    /playlists/{id}/songs # Ver canciones
POST   /playlists/{id}/songs # Agregar canción
GET    /users/{id}/playlists # Playlists de un usuario
PUT    /playlists/{id}       # Actualizar
DELETE /playlists/{id}/songs/{songId}  # Quitar canción
DELETE /playlists/{id}       # Eliminar playlist
```

## 🔧 Usar con Postman

1. Abre Postman
2. File → Import
3. Selecciona: `Music_Player_API.postman_collection.json`
4. ¡Listo! Prueba todos los endpoints

## 📦 Ejemplos

### Crear un Artista
```json
POST /api/v1/artists
{
  "name": "The Beatles",
  "biography": "Legendary rock band",
  "country": "UK",
  "image_url": "https://example.com/beatles.jpg"
}
```

### Crear un Álbum
```json
POST /api/v1/albums
{
  "title": "Abbey Road",
  "artist_id": 1,
  "release_date": "1969-09-26",
  "genre": "Rock",
  "cover_image_url": "https://example.com/abbey-road.jpg"
}
```

### Crear una Canción
```json
POST /api/v1/songs
{
  "title": "Come Together",
  "artist_id": 1,
  "album_id": 1,
  "duration_seconds": 259,
  "file_url": "https://example.com/song.mp3",
  "genre": "Rock"
}
```

### Crear una Playlist
```json
POST /api/v1/playlists
{
  "name": "My Favorites",
  "description": "Best songs",
  "user_id": 1,
  "is_public": true
}
```

### Agregar Canción a Playlist
```json
POST /api/v1/playlists/1/songs
{
  "song_id": 1,
  "position": 1
}
```

## 🏗️ Arquitectura

```
src/main/kotlin/com/example/
├── domain/models/        # Entidades (Artist, Album, Song, Playlist)
├── data/
│   ├── database/         # Tablas de BD (Exposed ORM)
│   └── repositories/     # Acceso a datos
├── service/              # Lógica de negocio
├── presentation/
│   ├── routes/           # Endpoints REST
│   └── dto/              # Request/Response models
└── config/               # Configuración (DB, CORS, Logging)
```

## 🛠️ Tecnologías

- **Kotlin** 2.2.20
- **Ktor** 3.3.2 (Framework web)
- **Exposed** 0.55.0 (ORM)
- **PostgreSQL** / H2 (Base de datos)
- **HikariCP** (Connection pooling)
- **Jackson** (JSON serialization)

## 📝 Configuración (.env)

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=music_player
DB_USER=music_admin
DB_PASSWORD=music1234
SERVER_PORT=8080
```

## ⚠️ Nota

Si PostgreSQL no está disponible, la app automáticamente usa H2 en memoria para desarrollo.

## 📚 Documentación

- **`ENDPOINTS.md`** - Lista rápida de todos los endpoints
- **`VER_TABLAS_POSTGRES.md`** - Cómo ver las tablas en PostgreSQL
- **`API_DOCUMENTATION.md`** - Documentación técnica completa
- **`ARCHITECTURE.md`** - Arquitectura del proyecto
- **`Music_Player_API.postman_collection.json`** - Colección de Postman

