# 📡 Endpoints de la API - Music Player

**URL Base:** `http://localhost:8080`

---

## ✅ Health Check

```http
GET /health
```

**Respuesta:**
```json
{
  "status": "healthy",
  "timestamp": 1701234567890
}
```

---

## 🎤 ARTISTAS

### Listar Artistas
```http
GET /api/v1/artists?limit=10&offset=0
```

### Crear Artista
```http
POST /api/v1/artists
Content-Type: application/json

{
  "name": "The Beatles",
  "biography": "Legendary rock band from Liverpool",
  "country": "UK",
  "image_url": "https://example.com/beatles.jpg"
}
```

### Obtener Artista
```http
GET /api/v1/artists/{id}
```

### Buscar Artistas
```http
GET /api/v1/artists/search?name=beatles
```

### Actualizar Artista
```http
PUT /api/v1/artists/{id}
Content-Type: application/json

{
  "name": "The Beatles (Updated)",
  "biography": "Updated bio"
}
```

### Eliminar Artista
```http
DELETE /api/v1/artists/{id}
```

---

## 💿 ÁLBUMES

### Listar Álbumes
```http
GET /api/v1/albums?limit=10&offset=0
```

### Crear Álbum
```http
POST /api/v1/albums
Content-Type: application/json

{
  "title": "Abbey Road",
  "artist_id": 1,
  "release_date": "1969-09-26",
  "genre": "Rock",
  "cover_image_url": "https://example.com/abbey-road.jpg"
}
```

### Obtener Álbum
```http
GET /api/v1/albums/{id}
```

### Álbumes de un Artista
```http
GET /api/v1/artists/{artistId}/albums
```

### Actualizar Álbum
```http
PUT /api/v1/albums/{id}
Content-Type: application/json

{
  "title": "Abbey Road (Remastered)",
  "genre": "Classic Rock"
}
```

### Eliminar Álbum
```http
DELETE /api/v1/albums/{id}
```

---

## 🎵 CANCIONES

### Listar Canciones
```http
GET /api/v1/songs?limit=10&offset=0
```

### Crear Canción
```http
POST /api/v1/songs
Content-Type: application/json

{
  "title": "Come Together",
  "artist_id": 1,
  "album_id": 1,
  "duration_seconds": 259,
  "file_url": "https://example.com/come-together.mp3",
  "genre": "Rock"
}
```

### Obtener Canción
```http
GET /api/v1/songs/{id}
```

### Reproducir Canción (incrementa play_count)
```http
POST /api/v1/songs/{id}/play
```

### Buscar Canciones
```http
GET /api/v1/songs/search?title=come
```

### Canciones de un Artista
```http
GET /api/v1/artists/{artistId}/songs
```

### Canciones de un Álbum
```http
GET /api/v1/albums/{albumId}/songs
```

### Actualizar Canción
```http
PUT /api/v1/songs/{id}
Content-Type: application/json

{
  "title": "Come Together (Remastered)",
  "duration_seconds": 260
}
```

### Eliminar Canción
```http
DELETE /api/v1/songs/{id}
```

---

## 📝 PLAYLISTS

### Listar Playlists
```http
GET /api/v1/playlists?limit=10&offset=0
```

### Crear Playlist
```http
POST /api/v1/playlists
Content-Type: application/json

{
  "name": "My Favorites",
  "description": "Best songs ever",
  "user_id": 1,
  "is_public": true,
  "cover_image_url": "https://example.com/playlist.jpg"
}
```

### Obtener Playlist
```http
GET /api/v1/playlists/{id}
```

### Ver Canciones de una Playlist
```http
GET /api/v1/playlists/{id}/songs
```

### Agregar Canción a Playlist
```http
POST /api/v1/playlists/{id}/songs
Content-Type: application/json

{
  "song_id": 1,
  "position": 1
}
```

### Playlists de un Usuario
```http
GET /api/v1/users/{userId}/playlists
```

### Actualizar Playlist
```http
PUT /api/v1/playlists/{id}
Content-Type: application/json

{
  "name": "My Top Favorites",
  "description": "Updated description"
}
```

### Eliminar Canción de Playlist
```http
DELETE /api/v1/playlists/{playlistId}/songs/{songId}
```

### Eliminar Playlist
```http
DELETE /api/v1/playlists/{id}
```

---

## 🔥 FLUJO COMPLETO DE PRUEBA

```bash
# 1. Crear artista
POST /api/v1/artists
{"name": "Pink Floyd", "country": "UK"}

# 2. Crear álbum
POST /api/v1/albums
{"title": "The Dark Side of the Moon", "artist_id": 1, "release_date": "1973-03-01"}

# 3. Crear canción
POST /api/v1/songs
{"title": "Time", "artist_id": 1, "album_id": 1, "duration_seconds": 413}

# 4. Crear playlist
POST /api/v1/playlists
{"name": "Classic Rock", "user_id": 1, "is_public": true}

# 5. Agregar canción a playlist
POST /api/v1/playlists/1/songs
{"song_id": 1, "position": 1}

# 6. Reproducir canción
POST /api/v1/songs/1/play

# 7. Ver playlist
GET /api/v1/playlists/1/songs
```

---

## 📥 Importar en Postman

**Archivo:** `Music_Player_API.postman_collection.json`

1. Abre Postman
2. File → Import
3. Selecciona el archivo
4. Todos los endpoints estarán disponibles con ejemplos

---

## 🎯 Total de Endpoints: **30+**

- ✅ Health: 1
- 🎤 Artistas: 6
- 💿 Álbumes: 6
- 🎵 Canciones: 9
- 📝 Playlists: 9
