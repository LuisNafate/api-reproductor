# 📊 Cómo Ver las Tablas en PostgreSQL

## Opción 1: pgAdmin (Interfaz Gráfica) ⭐ Recomendado

### Pasos:

1. **Abre pgAdmin**

2. **Navega en el panel izquierdo:**
   ```
   Servers
     └─ PostgreSQL 15 (o tu versión)
         └─ Databases
             └─ music_player
                 └─ Schemas
                     └─ public
                         └─ Tables  👈 AQUÍ están las tablas
   ```

3. **Verás 6 tablas:**
   - `users` ⭐ Nueva (autenticación)
   - `artists`
   - `albums`
   - `songs`
   - `playlists`
   - `playlist_songs`

4. **Para ver los datos:**
   - Click derecho en cualquier tabla
   - **View/Edit Data** → **All Rows**

5. **Para ver la estructura:**
   - Click derecho en la tabla
   - **Properties**
   - Pestaña **Columns** (ver columnas)

---

## Opción 2: Query Tool (SQL)

1. **En pgAdmin:**
   - Click derecho en la base de datos `music_player`
   - **Query Tool** (o presiona Alt+Shift+Q)

2. **Ejecuta estos comandos:**

### Ver todas las tablas
```sql
\dt
-- O si no funciona:
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public';
```

### Ver estructura de una tabla
```sql
\d artists
-- O:
SELECT column_name, data_type, character_maximum_length
FROM information_schema.columns
WHERE table_name = 'artists';
```

### Ver datos de las tablas
```sql
SELECT * FROM users;
SELECT * FROM artists;
SELECT * FROM albums;
SELECT * FROM songs;
SELECT * FROM playlists;
SELECT * FROM playlist_songs;
```
### Contar registros
```sql
SELECT 'users' as tabla, COUNT(*) as total FROM users
UNION ALL
SELECT 'artists', COUNT(*) FROM artists
UNION ALL
SELECT 'albums', COUNT(*) FROM albums
UNION ALL
SELECT 'songs', COUNT(*) FROM songs
UNION ALL
SELECT 'playlists', COUNT(*) FROM playlists
UNION ALL
SELECT 'playlist_songs', COUNT(*) FROM playlist_songs;
```ECT 'playlist_songs', COUNT(*) FROM playlist_songs;
```

### Ver canciones con información de artista y álbum
```sql
SELECT 
    s.id,
    s.title as cancion,
    a.name as artista,
    al.title as album,
    s.duration_seconds,
    s.play_count
FROM songs s
JOIN artists a ON s.artist_id = a.id
LEFT JOIN albums al ON s.album_id = al.id
ORDER BY s.play_count DESC;
```

### Ver playlists con sus canciones
```sql
SELECT 
    p.name as playlist,
    s.title as cancion,
    a.name as artista,
    ps.position
FROM playlists p
JOIN playlist_songs ps ON p.id = ps.playlist_id
JOIN songs s ON ps.song_id = s.id
JOIN artists a ON s.artist_id = a.id
ORDER BY p.id, ps.position;
```

### Ver todos los usuarios y sus roles
```sql
SELECT 
    id,
    username,
    email,
    role,
    created_at,
    updated_at
FROM users
ORDER BY created_at DESC;
```

### Contar usuarios por rol
```sql
SELECT 
    role,
    COUNT(*) as total
FROM users
GROUP BY role;
```

### Cambiar rol de usuario a ADMIN
```sql
-- Reemplaza 'tu_usuario' con el username real
UPDATE users 
SET role = 'ADMIN', updated_at = NOW() 
WHERE username = 'tu_usuario';
```

---

## Opción 3: SQL Shell (psql)

1. **Abre "SQL Shell (psql)" desde el menú inicio**

2. **Conectar:**
   ```
   Server [localhost]:         (presiona Enter)
   Database [postgres]:        music_player
   Port [5432]:               (presiona Enter)
   Username [postgres]:        music_admin
   Password:                   music1234
   ```

3. **Comandos útiles:**
   ```sql
   \l                  -- Listar bases de datos
   \c music_player     -- Conectar a music_player
   \dt                 -- Listar tablas
   \d artists          -- Describir tabla artists
   \q                  -- Salir
   ```

---

## 🔍 Verificar que las Tablas se Crearon

Después de iniciar tu aplicación (`./gradlew run`), las tablas se crean automáticamente.

**En el Query Tool de pgAdmin, ejecuta:**

```sql
SELECT 
    table_name,
    (SELECT COUNT(*) 
     FROM information_schema.columns 
     WHERE columns.table_name = tables.table_name) as columnas
FROM information_schema.tables
WHERE table_schema = 'public'
AND table_type = 'BASE TABLE'
ORDER BY table_name;
**Deberías ver:**
```
table_name       | columnas
-----------------|---------
albums           | 8
artists          | 6
playlist_songs   | 3
playlists        | 7
songs            | 10
users            | 7
```ylists        | 7
songs            | 10
```

---
## 📋 Estructura de las Tablas

### users (Usuarios) ⭐ Nueva
```sql
id              UUID PRIMARY KEY
username        VARCHAR(255) UNIQUE
password        VARCHAR(255)    -- BCrypt hash
email           VARCHAR(255) UNIQUE
role            VARCHAR(50)     -- USER, ADMIN
created_at      TIMESTAMP
updated_at      TIMESTAMP
```

### artists (Artistas)
### artists (Artistas)
```sql
id              INT PRIMARY KEY
name            VARCHAR(255)
biography       TEXT
country         VARCHAR(255)
image_url       VARCHAR(255)
created_at      TIMESTAMP
updated_at      TIMESTAMP
```

### albums (Álbumes)
```sql
id              INT PRIMARY KEY
title           VARCHAR(255)
artist_id       INT (FK → artists)
release_date    DATE
cover_image_url VARCHAR(255)
genre           VARCHAR(255)
created_at      TIMESTAMP
updated_at      TIMESTAMP
```

### songs (Canciones)
```sql
id              INT PRIMARY KEY
title           VARCHAR(255)
artist_id       INT (FK → artists)
album_id        INT (FK → albums, nullable)
duration_seconds INT
file_url        VARCHAR(255)
genre           VARCHAR(255)
play_count      INT (default 0)
created_at      TIMESTAMP
updated_at      TIMESTAMP
```

### playlists (Playlists)
```sql
id              INT PRIMARY KEY
name            VARCHAR(255)
description     TEXT
user_id         INT
is_public       BOOLEAN
cover_image_url VARCHAR(255)
created_at      TIMESTAMP
updated_at      TIMESTAMP
```

### playlist_songs (Relación Playlist-Canción)
```sql
playlist_id     INT (FK → playlists, PK)
song_id         INT (FK → songs, PK)
position        INT
-- PRIMARY KEY (playlist_id, song_id)
```

---

## ⚡ Atajo Rápido

**Para ver todo rápidamente en pgAdmin:**

1. Abre pgAdmin
2. Expande: Servers → PostgreSQL → Databases → music_player → Schemas → public → Tables
3. Click derecho en `artists` → View/Edit Data → All Rows
4. Repite para las demás tablas

¡Eso es todo! 🎉
