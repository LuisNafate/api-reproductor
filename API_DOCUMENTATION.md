# Music Player API 🎵

API REST para un reproductor de música estilo Spotify, construida con Kotlin y Ktor Framework.

## 🏗️ Arquitectura

El proyecto sigue una **arquitectura en capas** bien definida:

```
src/main/kotlin/com/example/
├── domain/              # Capa de Dominio
│   └── models/          # Entidades de negocio (Artist, Album, Song, Playlist)
├── data/                # Capa de Datos
│   ├── database/        # Definición de tablas (Exposed ORM)
│   └── repositories/    # Repositorios con interfaces e implementaciones
├── service/             # Capa de Negocio
│   └── *Service.kt      # Lógica de negocio y orquestación
├── presentation/        # Capa de Presentación
│   ├── routes/          # Endpoints REST
│   └── dto/             # DTOs (Request/Response)
├── config/              # Configuración
│   ├── DatabaseFactory.kt
│   ├── StatusPages.kt
│   ├── CORS.kt
│   └── Logging.kt
└── util/                # Utilidades
    └── Exceptions.kt
```

## 🚀 Características

- ✅ **CRUD completo** para Artistas, Álbumes, Canciones y Playlists
- ✅ **Búsqueda** de artistas y canciones
- ✅ **Relaciones** entre entidades (artista -> álbumes -> canciones)
- ✅ **Playlists** con gestión de canciones
- ✅ **Contador de reproducciones** para canciones
- ✅ **Paginación** en listados
- ✅ **Manejo de errores** robusto
- ✅ **CORS** configurado
- ✅ **Logging** de peticiones
- ✅ **PostgreSQL** como base de datos principal
- ✅ **H2** para testing

## 📋 Prerequisitos

- JDK 17 o superior
- PostgreSQL 12 o superior
- Gradle 8.0 o superior

## 🔧 Configuración

1. **Clonar el repositorio**

2. **Configurar la base de datos PostgreSQL**
   ```sql
   CREATE DATABASE music_db;
   ```

3. **Copiar el archivo de configuración**
   ```bash
   cp .env.example .env
   ```

4. **Editar las variables de entorno en `.env`**

5. **Ejecutar la aplicación**
   ```bash
   ./gradlew run
   ```

## 📚 API Endpoints

### Health Check
```
GET  /              # Info de la API
GET  /health        # Health check
```

### Artistas
```
GET    /api/v1/artists              # Listar artistas
GET    /api/v1/artists/{id}         # Obtener artista por ID
GET    /api/v1/artists/search?name= # Buscar artistas
POST   /api/v1/artists              # Crear artista
PUT    /api/v1/artists/{id}         # Actualizar artista
DELETE /api/v1/artists/{id}         # Eliminar artista
```

### Álbumes
```
GET    /api/v1/albums                  # Listar álbumes
GET    /api/v1/albums/{id}             # Obtener álbum por ID
GET    /api/v1/artists/{id}/albums     # Álbumes de un artista
POST   /api/v1/albums                  # Crear álbum
PUT    /api/v1/albums/{id}             # Actualizar álbum
DELETE /api/v1/albums/{id}             # Eliminar álbum
```

### Canciones
```
GET    /api/v1/songs                   # Listar canciones
GET    /api/v1/songs/{id}              # Obtener canción por ID
GET    /api/v1/songs/search?title=     # Buscar canciones
GET    /api/v1/artists/{id}/songs      # Canciones de un artista
GET    /api/v1/albums/{id}/songs       # Canciones de un álbum
POST   /api/v1/songs                   # Crear canción
POST   /api/v1/songs/{id}/play         # Incrementar contador
PUT    /api/v1/songs/{id}              # Actualizar canción
DELETE /api/v1/songs/{id}              # Eliminar canción
```

### Playlists
```
GET    /api/v1/playlists               # Listar playlists
GET    /api/v1/playlists/{id}          # Obtener playlist por ID
GET    /api/v1/users/{id}/playlists    # Playlists de un usuario
GET    /api/v1/playlists/{id}/songs    # Canciones de una playlist
POST   /api/v1/playlists               # Crear playlist
POST   /api/v1/playlists/{id}/songs    # Agregar canción a playlist
PUT    /api/v1/playlists/{id}          # Actualizar playlist
DELETE /api/v1/playlists/{id}          # Eliminar playlist
DELETE /api/v1/playlists/{id}/songs/{songId} # Remover canción
```

## 📝 Ejemplos de Uso

### Crear un Artista
```json
POST /api/v1/artists
{
  "name": "The Beatles",
  "biography": "Legendary rock band from Liverpool",
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
  "file_url": "https://example.com/come-together.mp3",
  "genre": "Rock"
}
```

### Crear una Playlist
```json
POST /api/v1/playlists
{
  "name": "My Favorites",
  "description": "Best songs ever",
  "user_id": 1,
  "is_public": true,
  "cover_image_url": "https://example.com/playlist.jpg"
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

## 🛠️ Tecnologías

- **Kotlin** 2.2.20
- **Ktor** 3.3.2 (Framework web)
- **Exposed** 0.55.0 (ORM)
- **PostgreSQL** (Base de datos)
- **HikariCP** (Connection pooling)
- **Jackson** (Serialización JSON)
- **Logback** (Logging)

## 🏛️ Patrones de Diseño

- **Repository Pattern**: Abstracción de acceso a datos
- **Service Layer**: Lógica de negocio separada
- **DTO Pattern**: Separación entre modelos de dominio y presentación
- **Dependency Injection**: Inyección manual de dependencias
- **Exception Handling**: Manejo centralizado de errores

## 🔐 Seguridad

El proyecto incluye configuración básica de JWT (actualmente comentada). Para implementar autenticación:

1. Descomentar la configuración en `Security.kt`
2. Configurar las variables JWT en `.env`
3. Agregar middleware de autenticación a las rutas protegidas

## 🧪 Testing

Para ejecutar con H2 en memoria (útil para tests):

```kotlin
DatabaseFactory.initH2()
```

## 📦 Build & Deploy

### Build JAR
```bash
./gradlew shadowJar
```

### Ejecutar JAR
```bash
java -jar build/libs/mi-api.jar
```

## 🤝 Contribuir

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT.
