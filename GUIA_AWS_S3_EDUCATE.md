# 🚀 Guía Completa: Configurar S3 y EC2 en AWS Academy Learner Lab

## 📋 Tabla de Contenidos
1. [Acceder a AWS Academy Learner Lab](#1-acceder-a-aws-academy-learner-lab)
2. [Crear Bucket S3](#2-crear-bucket-s3)
3. [Configurar Permisos Públicos](#3-configurar-permisos-públicos)
4. [Obtener Credenciales AWS](#4-obtener-credenciales-aws)
5. [Configurar Variables de Entorno](#5-configurar-variables-de-entorno)
6. [Subir Archivos de Prueba](#6-subir-archivos-de-prueba)
7. [Probar la API](#7-probar-la-api)

---

## 1. Acceder a AWS Academy Learner Lab

### Paso 1.1: Iniciar el Laboratorio

1. **Entra a tu curso en AWS Academy**
   - Ve a Canvas/Moodle donde tienes tu curso
   - Busca "AWS Academy Learner Lab"

2. **Inicia la sesión del lab**
   - Haz clic en **"Start Lab"** (botón verde)
   - **⏱️ IMPORTANTE:** El lab tiene un límite de tiempo (generalmente 4 horas)
   - Espera a que el círculo al lado de "AWS" cambie de 🔴 rojo a 🟢 verde

3. **Accede a la consola de AWS**
   - Una vez el círculo esté verde, haz clic en **"AWS"**
   - Se abrirá la consola de AWS Management Console

### ⚠️ Limitaciones de AWS Academy:
- **Créditos limitados** (generalmente $100)
- **Sesiones temporales** (4 horas máximo)
- **Credenciales temporales** (cambiarán cada vez que inicies el lab)
- No todos los servicios están disponibles

---

## 2. Crear Bucket S3

### Paso 2.1: Navegar a S3

1. En la consola de AWS, busca **"S3"** en la barra de búsqueda superior
2. Haz clic en **"S3"** para abrir el servicio

### Paso 2.2: Crear Nuevo Bucket

1. Haz clic en **"Create bucket"** (botón naranja)

2. **Configuración básica:**
   ```
   Bucket name: music-player-bucket-[tu-nombre-único]
   Ejemplo: music-player-bucket-luis2024
   ```
   - ⚠️ El nombre debe ser **globalmente único** (nadie más en AWS puede tenerlo)
   - Solo minúsculas, números y guiones
   - Sin espacios ni caracteres especiales

3. **AWS Region:** Selecciona **US East (N. Virginia) us-east-1**
   - Es la región más económica y rápida

4. **Object Ownership:**
   - Selecciona **"ACLs disabled (recommended)"**

5. **Block Public Access settings:**
   - **⚠️ IMPORTANTE:** Desmarca todas las casillas
   - ❌ Quita el check de **"Block all public access"**
   - Aparecerá un warning, marca **"I acknowledge..."**
   
   ```
   ❌ Block all public access: OFF
   ❌ Block public access to buckets and objects granted through new access control lists (ACLs)
   ❌ Block public access to buckets and objects granted through any access control lists (ACLs)
   ❌ Block public access to buckets and objects granted through new public bucket or access point policies
   ❌ Block public and cross-account access to buckets and objects through any public bucket or access point policies
   ```

6. **Bucket Versioning:** Deja en **Disable** (para ahorrar espacio)

7. **Encryption:** Deja **Server-side encryption disabled** (para ahorrar costos)

8. Haz clic en **"Create bucket"** al final de la página

---

## 3. Configurar Permisos Públicos

### Paso 3.1: Configurar Política del Bucket

1. Haz clic en el nombre de tu bucket (music-player-bucket-luis2024)

2. Ve a la pestaña **"Permissions"**

3. Baja hasta **"Bucket policy"** y haz clic en **"Edit"**

4. **Copia y pega esta política:**

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "PublicReadGetObject",
            "Effect": "Allow",
            "Principal": "*",
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::music-player-bucket-luis2024/*"
        }
    ]
}
```

5. **⚠️ IMPORTANTE:** Reemplaza `music-player-bucket-luis2024` con el nombre de TU bucket

6. Haz clic en **"Save changes"**

### ¿Qué hace esta política?
- Permite que **cualquier persona** pueda **ver/descargar** archivos del bucket
- Pero **NO** pueden subir, modificar o eliminar (eso solo lo hace tu API con credenciales)

---

## 4. Obtener Credenciales AWS

### Paso 4.1: Acceder a AWS Details

1. **Vuelve a la página del Learner Lab** (no cierres la consola de AWS)

2. Haz clic en **"AWS Details"** (arriba a la derecha, al lado de "Start Lab")

3. Verás algo así:

```
AWS CLI:
[default]
aws_access_key_id=ASIAUX...
aws_secret_access_key=wJalrXUtn...
aws_session_token=IQoJb3JpZ2lu...
```

### Paso 4.2: Copiar Credenciales

Copia estos 3 valores (los usaremos en el `.env`):

```bash
# Ejemplo (NO uses estos, usa los tuyos):
AWS_ACCESS_KEY_ID=ASIAUX6OZ7EXAMPLE
AWS_SECRET_ACCESS_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
AWS_SESSION_TOKEN=IQoJb3JpZ2luX2VjEH8aCXVzLWVhc3QtMSJIMEYCIQ... (muy largo)
```

### ⚠️ MUY IMPORTANTE:
- Estas credenciales **caducan** cuando finaliza el lab
- **NUNCA las subas a GitHub** (agrégalas al `.gitignore`)
- Cada vez que inicies el lab, deberás copiar las nuevas credenciales

---

## 5. Configurar Variables de Entorno

### Paso 5.1: Editar archivo `.env`

1. Abre el archivo `.env` en la raíz de tu proyecto

2. **Actualiza las credenciales de AWS:**

```bash
# ============================================
# AWS S3 Configuration
# ============================================
AWS_BUCKET=music-player-bucket-luis2024
AWS_REGION=us-east-1
AWS_ACCESS_KEY=ASIAUX6OZ7EXAMPLE
AWS_SECRET_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
AWS_SESSION_TOKEN=IQoJb3JpZ2luX2VjEH8aCXVzLWVhc3QtMSJIMEYCIQ...

# ============================================
# Database Configuration
# ============================================
DB_HOST=localhost
DB_PORT=5432
DB_NAME=music_player
DB_USER=postgres
DB_PASSWORD=1234

# ============================================
# JWT Configuration
# ============================================
JWT_SECRET=my-super-secret-key-change-this-in-production
JWT_DOMAIN=https://music-player.com
JWT_AUDIENCE=music-player-users
JWT_REALM=Music Player API
```

3. **Reemplaza:**
   - `AWS_BUCKET` → Nombre de tu bucket
   - `AWS_ACCESS_KEY` → Tu access key
   - `AWS_SECRET_KEY` → Tu secret key
   - `AWS_SESSION_TOKEN` → Tu session token (el más largo)

### Paso 5.2: Verificar `.gitignore`

Asegúrate de que `.env` esté en `.gitignore`:

```gitignore
# Environment variables
.env
.env.local
.env.*.local

# AWS credentials
aws-credentials.txt
```

---

## 6. Subir Archivos de Prueba

### Opción A: Subir manualmente desde AWS Console

1. **Entra a tu bucket** en la consola S3

2. Haz clic en **"Upload"**

3. **Sube imágenes de artistas:**
   - Crea una carpeta llamada `artists` (opcional)
   - Sube 3-5 imágenes de artistas (JPG/PNG, máx 5MB)
   - Ejemplo: `artist-beatles.jpg`, `artist-queen.jpg`

4. **Sube portadas de álbumes:**
   - Crea una carpeta llamada `albums` (opcional)
   - Sube 3-5 portadas de álbumes
   - Ejemplo: `album-abbey-road.jpg`

5. **Sube canciones:**
   - Crea una carpeta llamada `songs`
   - Sube 3-5 archivos MP3 (máx 10MB cada uno)
   - Ejemplo: `song-hey-jude.mp3`

6. Haz clic en **"Upload"**

### Opción B: Probar con URLs de prueba

Si no tienes archivos, usa estas URLs públicas temporalmente:

```kotlin
// En Postman, al crear artista sin imagen, usa estas URLs:
imageUrl: "https://via.placeholder.com/500x500.png?text=Artist"
coverImageUrl: "https://via.placeholder.com/500x500.png?text=Album"
```

---

## 7. Probar la API

### Paso 7.1: Iniciar el Servidor

```bash
# En la terminal:
./gradlew run
```

Deberías ver:
```
Application started in 1.728 seconds.
Responding at http://127.0.0.1:8080
```

### Paso 7.2: Registrar Usuario en Postman

**POST** `http://localhost:8080/api/v1/auth/register`

Headers:
```
Content-Type: application/json
```

Body (JSON):
```json
{
  "username": "admin",
  "email": "admin@test.com",
  "password": "admin123"
}
```

Respuesta esperada:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "admin",
    "email": "admin@test.com",
    "role": "USER"
  }
}
```

**⚠️ IMPORTANTE:** Copia el `token`, lo usarás en las siguientes peticiones.

### Paso 7.3: Actualizar Rol a ADMIN (Manual)

**Opción 1: Usando pgAdmin o psql**

```sql
UPDATE users 
SET role = 'ADMIN' 
WHERE username = 'admin';
```

**Opción 2: Crear usuario ADMIN desde código** (si modificas AuthService)

### Paso 7.4: Crear Artista con Imagen (MULTIPART)

**POST** `http://localhost:8080/api/v1/artists`

Headers:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

Body (form-data):
```
name: The Beatles
biography: Legendary British rock band
country: United Kingdom
image: [SELECCIONA ARCHIVO .jpg/.png]
```

**Pasos en Postman:**

1. Cambia Body a **"form-data"**
2. Agrega campos:
   - `name` → Text → "The Beatles"
   - `biography` → Text → "Legendary British rock band"
   - `country` → Text → "United Kingdom"
   - `image` → **File** → [Selecciona una imagen]

3. En **Headers**, agrega:
   ```
   Authorization: Bearer TU_TOKEN_AQUI
   ```

4. Haz clic en **Send**

Respuesta esperada:
```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "name": "The Beatles",
  "biography": "Legendary British rock band",
  "country": "United Kingdom",
  "imageUrl": "https://music-player-bucket-luis2024.s3.us-east-1.amazonaws.com/uuid-artist-The%20Beatles.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=...",
  "createdAt": "2024-11-28T12:30:00",
  "updatedAt": "2024-11-28T12:30:00"
}
```

**✅ ¡Imagen subida a S3 automáticamente!**

### Paso 7.5: Listar Artistas (Verificar URLs Firmadas)

**GET** `http://localhost:8080/api/v1/artists`

Respuesta:
```json
[
  {
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "name": "The Beatles",
    "biography": "Legendary British rock band",
    "country": "United Kingdom",
    "imageUrl": "https://music-player-bucket-luis2024.s3.us-east-1.amazonaws.com/uuid-artist-The%20Beatles.jpg?X-Amz-Algorithm=...",
    "createdAt": "2024-11-28T12:30:00",
    "updatedAt": "2024-11-28T12:30:00"
  }
]
```

**Copia el `imageUrl` y pégalo en tu navegador → ¡Deberías ver la imagen!**

### Paso 7.6: Crear Álbum con Portada

**POST** `http://localhost:8080/api/v1/albums`

Headers:
```
Authorization: Bearer TU_TOKEN
```

Body (form-data):
```
title: Abbey Road
year: 1969
artistId: a1b2c3d4-e5f6-7890-abcd-ef1234567890
genre: Rock
image: [SELECCIONA ARCHIVO .jpg/.png]
```

---

## 🔄 Renovar Credenciales (Cada Sesión)

### Cuando el lab expire:

1. **Detén el lab** (si aún está activo): Haz clic en "End Lab"

2. **Reinicia el lab:** Haz clic en "Start Lab"

3. **Obtén nuevas credenciales:**
   - AWS Details → Copia nuevas credenciales
   - Actualiza `.env` con los nuevos valores

4. **Reinicia tu servidor:**
   ```bash
   # Ctrl+C para detener
   ./gradlew run
   ```

---

## 🎯 Testing Completo

### Test 1: Crear 3 Artistas con Imágenes

```
POST /api/v1/artists (multipart)
- The Beatles + imagen
- Queen + imagen
- Pink Floyd + imagen
```

### Test 2: Listar Artistas

```
GET /api/v1/artists
→ Verificar que cada imageUrl es accesible (copiar en navegador)
```

### Test 3: Crear Álbum con Portada

```
POST /api/v1/albums (multipart)
- Abbey Road (artistId de The Beatles)
- A Night at the Opera (artistId de Queen)
```

### Test 4: Listar Álbumes

```
GET /api/v1/albums
→ Verificar URLs firmadas de portadas
```

### Test 5: GET Específico

```
GET /api/v1/artists/{id}
→ Verificar URL firmada individual
```

---

## 🐛 Troubleshooting

### Error: "Access Denied" al subir archivo

**Problema:** Las credenciales están mal o caducaron

**Solución:**
1. Ve a AWS Details en Learner Lab
2. Copia las credenciales actuales
3. Actualiza `.env`
4. Reinicia el servidor

### Error: "Bucket does not exist"

**Problema:** Nombre de bucket incorrecto

**Solución:**
1. Verifica el nombre en la consola S3
2. Actualiza `AWS_BUCKET` en `.env`

### Error: 403 Forbidden al acceder a imagen

**Problema:** Política del bucket no configurada

**Solución:**
1. Ve a bucket → Permissions → Bucket policy
2. Verifica que la política permita `s3:GetObject` para `"Principal": "*"`

### Imágenes no se ven en navegador

**Problema:** URL firmada caducó (12 horas)

**Solución:**
- Haz GET nuevamente al endpoint
- La API generará una nueva URL firmada

### Error: "Session token expired"

**Problema:** El lab caducó (4 horas)

**Solución:**
1. End Lab → Start Lab
2. Obtener nuevas credenciales
3. Actualizar `.env`

---

## 📊 Monitoreo de Costos

### Ver uso de S3:

1. Consola AWS → S3 → Tu bucket
2. Ve a **Metrics** → **Storage**
3. Verás cuántos GB estás usando

### Buenas prácticas:

- **No subas archivos muy grandes** (máx 10MB por canción)
- **Usa compresión** en imágenes (JPG con 70-80% calidad)
- **Elimina archivos de prueba** cuando termines

---

## ✅ Checklist Final

- [ ] Lab iniciado (círculo verde)
- [ ] Bucket S3 creado con nombre único
- [ ] Block Public Access desactivado
- [ ] Política del bucket configurada
- [ ] Credenciales AWS copiadas
- [ ] `.env` actualizado con credenciales
- [ ] Servidor Ktor iniciado sin errores
- [ ] Usuario ADMIN creado
- [ ] POST /artists con multipart funciona
- [ ] Imagen visible en navegador
- [ ] GET /artists devuelve URLs firmadas
- [ ] POST /albums con portada funciona

---

## 🎓 Siguiente Paso: EC2 (Opcional)

Si quieres **desplegar tu API en EC2** (servidor en la nube):

### Paso rápido:

1. **En AWS Console:** EC2 → Launch Instance
2. **Configuración:**
   - Name: music-player-api
   - AMI: Ubuntu 24.04 LTS
   - Instance type: t2.micro (free tier)
   - Key pair: Create new (descarga .pem)
   - Security Group: Allow HTTP (80), HTTPS (443), SSH (22), Custom (8080)

3. **Conectar vía SSH:**
   ```bash
   ssh -i "tu-key.pem" ubuntu@ec2-XX-XX-XX-XX.compute-1.amazonaws.com
   ```

4. **Instalar dependencias:**
   ```bash
   sudo apt update
   sudo apt install openjdk-17-jdk postgresql -y
   ```

5. **Subir tu proyecto:**
   ```bash
   # En tu PC:
   scp -i "tu-key.pem" build/libs/mi-api.jar ubuntu@ec2-XX-XX-XX-XX:/home/ubuntu/
   ```

6. **Ejecutar:**
   ```bash
   java -jar mi-api.jar
   ```

---

## 📚 Recursos Adicionales

- [AWS S3 Documentation](https://docs.aws.amazon.com/s3/)
- [AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/)
- [Ktor Multipart Support](https://ktor.io/docs/multipart-support.html)
- [AWS Academy Learner Lab Guide](https://awsacademy.instructure.com/)

---

**¡Tu API ahora funciona igual que el repositorio de referencia!** 🎉

- ✅ Multipart directo (imagen + datos)
- ✅ Subida automática a S3
- ✅ Solo KEY guardada en BD
- ✅ URLs firmadas generadas on-the-fly
- ✅ Compatible con AWS Academy Learner Lab
