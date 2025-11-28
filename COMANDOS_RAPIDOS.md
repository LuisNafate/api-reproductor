# 🚀 Comandos Rápidos - Referencia

## 🎯 Estado Actual

```
✅ Servidor: RUNNING en http://127.0.0.1:8080
✅ Base de datos: music_player (PostgreSQL)
✅ Compilación: SUCCESS
✅ Implementación: COMPLETA
```

---

## 📋 Comandos del Proyecto

### Compilar
```bash
./gradlew build --no-daemon
```

### Iniciar Servidor
```bash
./gradlew run
```

### Detener Servidor
```
Ctrl + C
```

### Limpiar y Recompilar
```bash
./gradlew clean build --no-daemon
```

---

## 🗄️ Base de Datos (PostgreSQL)

### Conectar con psql
```bash
psql -U postgres -d music_player
```

### Actualizar usuario a ADMIN
```sql
UPDATE users SET role = 'ADMIN' WHERE username = 'admin';
```

### Ver artistas con KEYs
```sql
SELECT id, name, image_url FROM artists;
```

### Ver álbumes con KEYs
```sql
SELECT id, title, cover_image_url FROM albums;
```

### Eliminar datos de prueba
```sql
DELETE FROM artists;
DELETE FROM albums;
DELETE FROM users WHERE username = 'admin';
```

---

## 🔑 Endpoints Principales

### Base URL
```
http://localhost:8080/api/v1
```

### Autenticación

#### Registrar Usuario
```http
POST /auth/register
Content-Type: application/json

{
  "username": "admin",
  "email": "admin@test.com",
  "password": "admin123"
}
```

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

### Artistas (Multipart)

#### Crear Artista con Imagen
```http
POST /artists
Authorization: Bearer [TOKEN]
Content-Type: multipart/form-data

Form Data:
- name: The Beatles
- biography: Legendary British rock band
- country: United Kingdom
- image: [FILE beatles.jpg]
```

#### Listar Artistas
```http
GET /artists
```

#### Obtener Artista por ID
```http
GET /artists/{uuid}
```

#### Buscar Artistas
```http
GET /artists/search?name=Beatles
```

### Álbumes (Multipart)

#### Crear Álbum con Portada
```http
POST /albums
Authorization: Bearer [TOKEN]
Content-Type: multipart/form-data

Form Data:
- title: Abbey Road
- year: 1969
- artistId: [UUID_ARTISTA]
- genre: Rock
- image: [FILE abbey-road.jpg]
```

#### Listar Álbumes
```http
GET /albums
```

#### Álbumes por Artista
```http
GET /artists/{artistId}/albums
```

---

## 🪣 AWS S3 - Renovar Credenciales

### Pasos Rápidos (Cada Sesión)

1. **AWS Academy → Start Lab** (esperar 🟢)

2. **AWS Details** → Copiar credenciales:
   ```
   aws_access_key_id=ASIAUX...
   aws_secret_access_key=wJalrXU...
   aws_session_token=IQoJb3JpZ2lu...
   ```

3. **Actualizar `.env`:**
   ```bash
   AWS_BUCKET=music-player-bucket-tu-nombre
   AWS_REGION=us-east-1
   AWS_ACCESS_KEY=ASIAUX...
   AWS_SECRET_KEY=wJalrXU...
   AWS_SESSION_TOKEN=IQoJb3JpZ2lu...
   ```

4. **Reiniciar servidor:**
   ```bash
   # Ctrl+C
   ./gradlew run
   ```

---

## 🧪 Tests en Postman

### Test 1: Crear Usuario ADMIN

```javascript
// POST /auth/register
pm.test("Usuario creado", function() {
    pm.response.to.have.status(201);
    pm.environment.set("auth_token", pm.response.json().token);
});

// Luego en BD:
// UPDATE users SET role = 'ADMIN' WHERE username = 'admin';
```

### Test 2: Crear Artista con Imagen

```javascript
// POST /artists (multipart)
pm.test("Artista creado con imagen", function() {
    pm.response.to.have.status(201);
    var artist = pm.response.json();
    pm.expect(artist.imageUrl).to.include("s3.amazonaws.com");
    pm.expect(artist.imageUrl).to.include("X-Amz-Algorithm");
    pm.environment.set("artist_id", artist.id);
});
```

### Test 3: Verificar URLs Firmadas

```javascript
// GET /artists
pm.test("URLs firmadas presentes", function() {
    var artists = pm.response.json();
    artists.forEach(function(artist) {
        pm.expect(artist.imageUrl).to.include("X-Amz-Signature");
    });
});
```

---

## 🐛 Troubleshooting Rápido

### Error: "Access Denied" en S3
```bash
# Solución: Renovar credenciales
1. AWS Details → Copiar nuevas keys
2. Actualizar .env
3. Reiniciar servidor
```

### Error: "Bucket does not exist"
```bash
# Solución: Verificar nombre del bucket
1. AWS Console → S3 → Ver nombre exacto
2. Actualizar AWS_BUCKET en .env
```

### Error: 403 Forbidden al ver imagen
```bash
# Solución: Verificar política del bucket
1. S3 → Tu bucket → Permissions → Bucket policy
2. Verificar que permite s3:GetObject para "*"
```

### Error: "Session token expired"
```bash
# Solución: Lab caducó
1. End Lab → Start Lab
2. AWS Details → Copiar nuevas credenciales
3. Actualizar .env
4. Reiniciar servidor
```

---

## 📊 Verificación de S3

### Ver archivos en bucket (AWS Console)
```
1. AWS Console → S3
2. Click en tu bucket
3. Deberías ver: uuid-artist-...jpg, uuid-album-...jpg
```

### Verificar KEY en BD
```sql
SELECT name, image_url FROM artists;
-- Debería mostrar: "uuid-artist-Beatles.jpg" (no URL completa)
```

### Verificar URL firmada
```bash
# Copiar imageUrl de GET /artists
# Pegar en navegador
# Debería mostrar la imagen
```

---

## 🎯 Flujo Completo de Testing

```bash
# 1. Servidor corriendo
./gradlew run

# 2. Registrar usuario
POST /auth/register → Obtener token

# 3. Actualizar a ADMIN
psql → UPDATE users SET role = 'ADMIN'

# 4. Crear artista con imagen
POST /artists (multipart) → Obtener artist_id

# 5. Verificar imagen
GET /artists → Copiar imageUrl → Pegar en navegador

# 6. Crear álbum con portada
POST /albums (multipart, usar artist_id)

# 7. Verificar álbumes por artista
GET /artists/{artist_id}/albums → URLs firmadas

# 8. Repetir con 2-3 artistas más
```

---

## 📁 Archivos de Documentación

| Archivo | Propósito |
|---------|-----------|
| **GUIA_AWS_S3_EDUCATE.md** | Guía completa AWS S3 paso a paso |
| **COMPARACION_REPO_REFERENCIA.md** | Antes vs Ahora, diferencias técnicas |
| **RESUMEN_IMPLEMENTACION.md** | Estado actual y próximos pasos |
| **COMANDOS_RAPIDOS.md** | Este archivo - referencia rápida |
| **AWS_S3_GUIDE.md** | Guía original S3 |

---

## 🔗 URLs Importantes

| Servicio | URL |
|----------|-----|
| API Local | http://localhost:8080 |
| Health Check | http://localhost:8080/health |
| AWS Console | https://console.aws.amazon.com |
| AWS Academy | https://awsacademy.instructure.com |

---

## 🎓 Conceptos Clave

### KEY vs URL
```
❌ ANTES (URL completa en BD):
image_url: "https://bucket.s3.amazonaws.com/uuid-artist.jpg"

✅ AHORA (KEY en BD):
image_url: "uuid-artist-Beatles.jpg"

✅ URL firmada en respuesta:
"https://bucket.s3.amazonaws.com/uuid-artist-Beatles.jpg?X-Amz-Algorithm=..."
```

### Flujo de Upload
```
1. Cliente → POST /artists (multipart: datos + imagen)
2. ArtistRoutes → Extrae imageBytes
3. ArtistService → s3Service.uploadFile(imageBytes)
4. S3Service → Sube a S3 → Retorna KEY
5. Repository → Guarda KEY en BD
6. S3Service → getPresignedUrl(KEY) → URL firmada
7. Respuesta → Cliente recibe URL firmada (12h validez)
```

### Regeneración de URLs
```
GET /artists
→ Por cada artista:
   - Lee KEY de BD
   - Genera URL firmada fresca
   - Retorna en respuesta
→ URLs diferentes cada vez (nueva firma)
```

---

## 🚀 Siguiente Acción

### ⭐ Prioridad 1: Configurar AWS S3

```bash
# 1. Abrir guía
code GUIA_AWS_S3_EDUCATE.md

# 2. Seguir pasos 1-5 (20 min)
- Start Lab
- Crear bucket
- Configurar permisos
- Obtener credenciales
- Actualizar .env

# 3. Probar
POST /artists con imagen
GET /artists → Verificar URL firmada
```

---

## 💡 Tips Finales

### 1. **Credenciales AWS**
- ⏱️ Caducan en 4 horas
- 📋 Guarda pasos para renovar rápido
- 🔒 NUNCA subas a GitHub

### 2. **Testing**
- ✅ Siempre verifica URL en navegador
- ✅ Compara URLs de POST vs GET (diferentes firmas)
- ✅ Espera 1 min entre GET para ver cambio de firma

### 3. **Base de Datos**
- ✅ Solo KEYs en image_url/cover_image_url
- ✅ URLs se generan on-the-fly
- ✅ Elimina datos de prueba frecuentemente

### 4. **S3 Costs**
- 💰 S3 es barato (~$0.023/GB)
- 🗑️ Elimina archivos de prueba
- 📏 Limita archivos a 5-10MB

---

**¡Tu API está lista! Siguiente paso: Configurar AWS S3** 🚀
