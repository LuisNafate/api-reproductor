# 🚀 Guía Completa: Configurar AWS Academy Learner Lab para Music Player API

## 📋 Tabla de Contenidos
1. [Introducción](#introducción)
2. [Requisitos Previos](#requisitos-previos)
3. [Parte 1: Configurar AWS Academy Learner Lab](#parte-1-configurar-aws-academy-learner-lab)
4. [Parte 2: Crear y Configurar Bucket S3](#parte-2-crear-y-configurar-bucket-s3)
5. [Parte 3: Obtener Credenciales AWS](#parte-3-obtener-credenciales-aws)
6. [Parte 4: Configurar el Proyecto](#parte-4-configurar-el-proyecto)
7. [Parte 5: Probar la Integración](#parte-5-probar-la-integración)
8. [Parte 6: Renovar Credenciales](#parte-6-renovar-credenciales)
9. [Troubleshooting](#troubleshooting)
10. [Checklist Final](#checklist-final)

---

## Introducción

Esta guía te enseñará a:
- ✅ Configurar AWS Academy Learner Lab para tu API
- ✅ Crear un bucket S3 para almacenar archivos multimedia
- ✅ Subir imágenes de artistas, portadas de álbumes y canciones MP3
- ✅ Generar URLs firmadas automáticamente desde tu API
- ✅ Gestionar credenciales temporales de AWS Academy

### ⚠️ Limitaciones de AWS Academy Learner Lab

| Característica | Límite |
|----------------|--------|
| Duración por sesión | 4 horas |
| Créditos totales | $100 USD |
| Credenciales | Temporales (cambian cada sesión) |
| Instancias EC2 | Se suspenden al terminar |
| Bucket S3 | Persiste entre sesiones |
| Duración del curso | 6 meses máximo |

---

## Requisitos Previos

Antes de comenzar, asegúrate de tener:

- [x] Acceso a AWS Academy Learner Lab (Canvas/Moodle)
- [x] PostgreSQL instalado y configurado (ver `VER_TABLAS_POSTGRES.md`)
- [x] IntelliJ IDEA o tu IDE favorito
- [x] Postman instalado
- [x] Git configurado
- [x] JDK 17 o superior

---

## Parte 1: Configurar AWS Academy Learner Lab

### Paso 1.1: Acceder al Laboratorio

1. **Inicia sesión en AWS Academy**
   - Ve a Canvas o la plataforma donde tienes tu curso
   - Busca "AWS Academy Learner Lab"
   - Haz clic en el módulo del laboratorio

2. **Inicia el Lab**
   - Haz clic en el botón verde **"Start Lab"**
   - ⏱️ **IMPORTANTE:** El laboratorio solo está disponible por 4 horas
   - Espera a que el círculo junto a "AWS" cambie de 🔴 rojo a 🟢 verde
   - Esto puede tomar 1-2 minutos

   ```
   ⏳ Iniciando...
   🔴 AWS (Starting...)
   ↓
   ✅ Lab iniciado
   🟢 AWS (Ready)
   ```

3. **Accede a la Consola de AWS**
   - Una vez que el círculo esté 🟢 verde, haz clic en **"AWS"**
   - Se abrirá la AWS Management Console en una nueva pestaña
   - Verás que estás conectado como "voclabs/user..."

### Paso 1.2: Verificar Región

1. En la esquina superior derecha de la consola AWS, verifica la región
2. **Cambia a US East (N. Virginia) `us-east-1`** si no está seleccionada
3. Esta es la región más económica y rápida

   ```
   Región recomendada: us-east-1 (N. Virginia)
   ```

---

## Parte 2: Crear y Configurar Bucket S3

### Paso 2.1: Navegar al Servicio S3

1. En la consola AWS, busca **"S3"** en la barra de búsqueda superior
2. Haz clic en **"S3"** (Storage service)
3. Verás la lista de buckets (probablemente vacía si es tu primera vez)

### Paso 2.2: Crear Bucket S3

1. Haz clic en el botón naranja **"Create bucket"**

2. **Configuración General:**

   ```
   Bucket name: music-player-luis-2025
   ```
   
   ⚠️ **Reglas para el nombre:**
   - Debe ser **globalmente único** (nadie más en AWS puede tenerlo)
   - Solo minúsculas, números y guiones (`-`)
   - Sin espacios ni caracteres especiales
   - Entre 3-63 caracteres
   
   **Ejemplos válidos:**
   - `music-player-luis-2025`
   - `reproductor-musica-nafate`
   - `api-music-luis-upchiapas`

3. **AWS Region:**
   ```
   Selecciona: US East (N. Virginia) us-east-1
   ```

4. **Object Ownership:**
   ```
   ☑️ ACLs disabled (recommended)
   ```
   - Deja esta opción marcada

5. **⚠️ Block Public Access settings (CRÍTICO):**

   ```
   ❌ Desmarca "Block all public access"
   ```
   
   Asegúrate de **DESMARCAR** todas estas casillas:
   - ❌ Block all public access
   - ❌ Block public access to buckets and objects granted through new ACLs
   - ❌ Block public access to buckets and objects granted through any ACLs
   - ❌ Block public access to buckets and objects granted through new bucket policies
   - ❌ Block public and cross-account access to buckets through any bucket policies
   
   Aparecerá un **warning amarillo**, marca la casilla:
   ```
   ☑️ I acknowledge that the current settings might result in this bucket 
      and the objects within becoming public
   ```

6. **Bucket Versioning:**
   ```
   ⚪ Disable (para ahorrar espacio)
   ```

7. **Default encryption:**
   ```
   ⚪ Disable (para ahorrar costos)
   ```

8. Haz clic en el botón naranja **"Create bucket"** al final

9. ✅ **Verás tu bucket en la lista**

### Paso 2.3: Configurar Política de Bucket (Permisos Públicos)

Ahora haremos que los archivos sean accesibles públicamente:

1. **Entra al bucket:**
   - Haz clic en el nombre de tu bucket (`music-player-luis-2025`)

2. **Ve a la pestaña Permissions:**
   - Haz clic en **"Permissions"** (arriba)

3. **Edita la Bucket Policy:**
   - Baja hasta la sección **"Bucket policy"**
   - Haz clic en **"Edit"**

4. **Copia y pega esta política JSON:**

   ```json
   {
       "Version": "2012-10-17",
       "Statement": [
           {
               "Sid": "PublicReadGetObject",
               "Effect": "Allow",
               "Principal": "*",
               "Action": "s3:GetObject",
               "Resource": "arn:aws:s3:::music-player-luis-2025/*"
           }
       ]
   }
   ```

5. **⚠️ IMPORTANTE:** Reemplaza `music-player-luis-2025` con **TU nombre de bucket**

   ```json
   "Resource": "arn:aws:s3:::TU-BUCKET-AQUI/*"
   ```

6. Haz clic en **"Save changes"**

### ¿Qué hace esta política?

```
✅ Permite: Cualquier persona puede VER/DESCARGAR archivos
❌ Prohíbe: Nadie puede SUBIR, MODIFICAR o ELIMINAR (solo tu API puede)
```

---

## Parte 3: Obtener Credenciales AWS

### Paso 3.1: Acceder a AWS Details

1. **Vuelve a la pestaña de AWS Academy Learner Lab**
   - NO cierres la consola de AWS
   - Vuelve a la página donde hiciste "Start Lab"

2. **Haz clic en "AWS Details"**
   - Ubicado arriba a la derecha, al lado de "Start Lab"

3. **Verás algo así:**

   ```bash
   AWS CLI:
   [default]
   aws_access_key_id=ASIAUX6OZ7EXAMPLE123
   aws_secret_access_key=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
   aws_session_token=IQoJb3JpZ2luX2VjEH8aCXVzLWVhc3QtMSJIMEYCIQD...(muy largo)
   ```

### Paso 3.2: Copiar Credenciales

⚠️ **MUY IMPORTANTE:**
- Estas credenciales **CADUCAN** cuando finaliza el lab (4 horas)
- **NUNCA** las subas a GitHub
- Cada vez que inicies el lab, obtendrás **nuevas credenciales**

**Guarda estos 3 valores en un lugar temporal** (Notepad, Notes, etc.):

```bash
# EJEMPLO (NO uses estos, usa los tuyos):
AWS_ACCESS_KEY_ID=ASIAUX6OZ7EXAMPLE123
AWS_SECRET_ACCESS_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
AWS_SESSION_TOKEN=IQoJb3JpZ2luX2VjEH8aCXVzLWVhc3QtMSJIMEYCIQD...
```

---

## Parte 4: Configurar el Proyecto

### Paso 4.1: Crear/Editar archivo `.env`

1. **En la raíz de tu proyecto**, crea un archivo llamado `.env` si no existe:

   ```
   api-reproductor/
   ├── src/
   ├── build.gradle.kts
   ├── .gitignore
   └── .env  ← Crear aquí
   ```

2. **Copia este contenido al archivo `.env`:**

   ```bash
   # ============================================
   # AWS S3 Configuration
   # ============================================
   AWS_BUCKET=music-player-luis-2025
   AWS_REGION=us-east-1
   AWS_ACCESS_KEY=ASIAUX6OZ7EXAMPLE123
   AWS_SECRET_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
   AWS_SESSION_TOKEN=IQoJb3JpZ2luX2VjEH8aCXVzLWVhc3QtMSJIMEYCIQD...
   
   # ============================================
   # Database Configuration
   # ============================================
   DB_HOST=localhost
   DB_PORT=5432
   DB_NAME=music_player
   DB_USER=music_admin
   DB_PASSWORD=music1234
   
   # ============================================
   # JWT Configuration (si aplica)
   # ============================================
   JWT_SECRET=my-super-secret-key-change-this-in-production
   JWT_DOMAIN=https://music-player.com
   JWT_AUDIENCE=music-player-users
   JWT_REALM=Music Player API
   
   # ============================================
   # Server Configuration
   # ============================================
   SERVER_PORT=8080
   ```

3. **Reemplaza los valores de AWS:**
   - `AWS_BUCKET` → Tu nombre de bucket
   - `AWS_ACCESS_KEY` → Tu access key (empieza con ASIA...)
   - `AWS_SECRET_KEY` → Tu secret key (40+ caracteres)
   - `AWS_SESSION_TOKEN` → Tu session token (el MÁS LARGO, 500+ caracteres)

### Paso 4.2: Verificar `.gitignore`

**CRÍTICO:** Asegúrate de que `.env` esté en `.gitignore`

1. Abre el archivo `.gitignore` en la raíz del proyecto

2. Verifica que contenga:

   ```gitignore
   # Environment variables
   .env
   .env.local
   .env.*.local
   
   # AWS credentials
   aws-credentials.txt
   credentials.txt
   ```

3. Si no existe, agrégalo

### Paso 4.3: Instalar Dependencias (si es necesario)

Tu `build.gradle.kts` ya debería tener las dependencias AWS SDK. Verifica:

```kotlin
// AWS SDK for S3
implementation("aws.sdk.kotlin:s3:1.0.0")
implementation("aws.smithy.kotlin:aws-signing-default:1.0.0")
```

Si no están, agrégalas y ejecuta:

```bash
./gradlew build
```

---

## Parte 5: Probar la Integración

### Paso 5.1: Iniciar el Servidor

1. **En la terminal:**

   ```bash
   ./gradlew run
   ```

2. **Deberías ver:**

   ```
   Application started in 1.728 seconds.
   Responding at http://127.0.0.1:8080
   ```

### Paso 5.2: Probar Health Check

**GET** `http://localhost:8080/health`

Respuesta esperada:
```json
{
  "status": "OK"
}
```

### Paso 5.3: Registrar Usuario (si tienes autenticación JWT)

**POST** `http://localhost:8080/api/v1/auth/register`

Headers:
```
Content-Type: application/json
```

Body:
```json
{
  "username": "admin",
  "email": "admin@test.com",
  "password": "admin123"
}
```

Respuesta:
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

**⚠️ Copia el token, lo usarás en las siguientes peticiones.**

### Paso 5.4: Actualizar Usuario a ADMIN

**Opción 1: Usando pgAdmin**

1. Abre pgAdmin
2. Conecta a `music_player` database
3. Ejecuta:

   ```sql
   UPDATE users 
   SET role = 'ADMIN' 
   WHERE username = 'admin';
   ```

**Opción 2: Usando psql**

```bash
psql -U music_admin -d music_player

UPDATE users SET role = 'ADMIN' WHERE username = 'admin';
\q
```

### Paso 5.5: Subir Imagen de Artista (Multipart)

Ahora viene la parte importante: subir archivos a S3.

**POST** `http://localhost:8080/api/v1/artists`

Headers:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

Body (form-data):
```
name: The Beatles
biography: Legendary British rock band from Liverpool
country: United Kingdom
image: [SELECCIONA ARCHIVO .jpg o .png]
```

**Pasos en Postman:**

1. Cambia Body a **"form-data"**
2. Agrega los campos:
   - `name` → Text → "The Beatles"
   - `biography` → Text → "Legendary British rock band from Liverpool"
   - `country` → Text → "United Kingdom"
   - `image` → **File** → [Click "Select Files" y elige una imagen]

3. En **Headers**, agrega:
   ```
   Authorization: Bearer TU_TOKEN_AQUI
   ```

4. Haz clic en **Send**

**Respuesta esperada:**

```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "name": "The Beatles",
  "biography": "Legendary British rock band from Liverpool",
  "country": "United Kingdom",
  "imageUrl": "https://music-player-luis-2025.s3.us-east-1.amazonaws.com/artists/uuid-The-Beatles.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=...",
  "createdAt": "2025-11-28T22:30:00",
  "updatedAt": "2025-11-28T22:30:00"
}
```

**✅ ¡La imagen se subió automáticamente a S3!**

### Paso 5.6: Verificar Imagen en S3

1. **Copia el `imageUrl` de la respuesta**
2. **Pégalo en tu navegador**
3. **Deberías ver la imagen del artista**

**Alternativamente, en la consola AWS:**

1. Ve a S3 → Tu bucket
2. Verás una carpeta `artists/`
3. Dentro estará tu imagen

### Paso 5.7: Listar Artistas

**GET** `http://localhost:8080/api/v1/artists`

Respuesta:
```json
[
  {
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "name": "The Beatles",
    "biography": "Legendary British rock band from Liverpool",
    "country": "United Kingdom",
    "imageUrl": "https://music-player-luis-2025.s3.us-east-1.amazonaws.com/artists/uuid-The-Beatles.jpg?X-Amz-Algorithm=...",
    "createdAt": "2025-11-28T22:30:00",
    "updatedAt": "2025-11-28T22:30:00"
  }
]
```

Nota que el `imageUrl` es una **URL firmada** generada automáticamente.

### Paso 5.8: Crear Álbum con Portada

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
image: [SELECCIONA ARCHIVO .jpg de la portada]
```

### Paso 5.9: Crear Canción con Archivo MP3

**POST** `http://localhost:8080/api/v1/songs`

Headers:
```
Authorization: Bearer TU_TOKEN
```

Body (form-data):
```
title: Come Together
artistId: a1b2c3d4-e5f6-7890-abcd-ef1234567890
albumId: [ID del álbum creado]
durationSeconds: 259
genre: Rock
audio: [SELECCIONA ARCHIVO .mp3]
```

**✅ El archivo MP3 se subirá a S3 y se generará una URL firmada.**

---

## Parte 6: Renovar Credenciales

### Cada vez que el Lab expire (4 horas):

1. **Detén el lab (si aún está activo):**
   - En AWS Academy, haz clic en **"End Lab"**

2. **Reinicia el lab:**
   - Haz clic en **"Start Lab"**
   - Espera a que el círculo cambie a 🟢 verde

3. **Obtén nuevas credenciales:**
   - AWS Details → Copia las nuevas credenciales
   - Actualiza el archivo `.env` con los nuevos valores

4. **Reinicia tu servidor:**
   ```bash
   # Ctrl+C para detener el servidor
   ./gradlew run
   ```

### Script de Ayuda (opcional)

Crea un archivo `renovar-credenciales.sh`:

```bash
#!/bin/bash
echo "=================================="
echo "Renovar Credenciales AWS Academy"
echo "=================================="
echo ""
echo "Pega la línea AWS_ACCESS_KEY_ID:"
read access_key
echo "Pega la línea AWS_SECRET_ACCESS_KEY:"
read secret_key
echo "Pega la línea AWS_SESSION_TOKEN:"
read session_token

echo ""
echo "Actualizando .env..."
sed -i "s/AWS_ACCESS_KEY=.*/AWS_ACCESS_KEY=$access_key/" .env
sed -i "s/AWS_SECRET_KEY=.*/AWS_SECRET_KEY=$secret_key/" .env
sed -i "s/AWS_SESSION_TOKEN=.*/AWS_SESSION_TOKEN=$session_token/" .env

echo "✅ Credenciales actualizadas"
echo "Reinicia el servidor con: ./gradlew run"
```

---

## Troubleshooting

### Error: "Access Denied" al subir archivo

**Problema:** Las credenciales están mal o caducaron.

**Solución:**
1. Ve a AWS Details en Learner Lab
2. Copia las credenciales actuales
3. Actualiza `.env`
4. Reinicia el servidor: `./gradlew run`

### Error: "Bucket does not exist"

**Problema:** Nombre de bucket incorrecto en `.env`.

**Solución:**
1. Verifica el nombre en la consola S3
2. Actualiza `AWS_BUCKET` en `.env`
3. Reinicia el servidor

### Error: 403 Forbidden al acceder a imagen

**Problema:** Política del bucket no configurada correctamente.

**Solución:**
1. Ve a S3 → Tu bucket → Permissions → Bucket policy
2. Verifica que la política permita `s3:GetObject` para `"Principal": "*"`
3. Asegúrate de que "Block Public Access" esté desactivado

### Imagen no se ve en navegador

**Problema:** URL firmada caducó (válida por 12 horas por defecto).

**Solución:**
- Haz GET nuevamente al endpoint del artista/álbum/canción
- La API generará una nueva URL firmada

### Error: "Session token expired"

**Problema:** El lab caducó (4 horas).

**Solución:**
1. End Lab → Start Lab
2. AWS Details → Copiar nuevas credenciales
3. Actualizar `.env`
4. Reiniciar servidor

### Error: "Budget exceeded"

**⚠️ CRÍTICO:** Excediste los $100 de crédito.

**Solución:**
- Revisa el uso en: AWS Console → Billing Dashboard
- Elimina recursos innecesarios
- Si excedes el límite, **pierdes acceso al lab**

### No aparece la carpeta `artists/` en S3

**Problema:** Aún no has subido ningún artista.

**Solución:**
- Sube al menos un artista con imagen
- S3 crea las carpetas automáticamente al subir el primer archivo

---

## Checklist Final

### AWS Academy
- [ ] Lab iniciado (círculo 🟢 verde)
- [ ] Región configurada en `us-east-1`
- [ ] Credenciales copiadas de "AWS Details"

### S3
- [ ] Bucket creado con nombre único
- [ ] "Block Public Access" desactivado
- [ ] Política del bucket configurada correctamente
- [ ] Bucket accesible desde consola AWS

### Proyecto
- [ ] Archivo `.env` creado y configurado
- [ ] `.env` incluido en `.gitignore`
- [ ] Credenciales AWS actualizadas en `.env`
- [ ] PostgreSQL corriendo (puerto 5432)
- [ ] Base de datos `music_player` creada

### Testing
- [ ] Servidor iniciado sin errores (`./gradlew run`)
- [ ] Health check funciona (`GET /health`)
- [ ] Usuario ADMIN creado
- [ ] POST /artists con imagen funciona
- [ ] Imagen visible en navegador (URL firmada)
- [ ] GET /artists devuelve URLs firmadas
- [ ] POST /albums con portada funciona
- [ ] POST /songs con MP3 funciona

### Documentación
- [ ] Collection de Postman importada
- [ ] Endpoints probados en Postman
- [ ] Archivos de prueba preparados (imágenes, MP3s)

---

## 🎯 Siguiente Paso: Crear Datos de Prueba Completos

### Artistas Sugeridos

1. **The Beatles**
   - Imagen: Foto de los 4 miembros
   - Álbumes: Abbey Road, Let It Be

2. **Queen**
   - Imagen: Foto de Freddie Mercury y banda
   - Álbumes: A Night at the Opera, The Game

3. **Pink Floyd**
   - Imagen: Logo o portada de The Wall
   - Álbumes: The Dark Side of the Moon, The Wall

### Estructura Ideal en S3

```
music-player-luis-2025/
├── artists/
│   ├── uuid-The-Beatles.jpg
│   ├── uuid-Queen.jpg
│   └── uuid-Pink-Floyd.jpg
├── albums/
│   ├── uuid-Abbey-Road.jpg
│   ├── uuid-A-Night-at-the-Opera.jpg
│   └── uuid-The-Dark-Side-of-the-Moon.jpg
└── songs/
    ├── uuid-Come-Together.mp3
    ├── uuid-Bohemian-Rhapsody.mp3
    └── uuid-Money.mp3
```

---

## 📊 Monitoreo de Costos

### Ver Uso de S3

1. Consola AWS → S3 → Tu bucket
2. Ve a **Metrics** → **Storage**
3. Verás cuántos GB estás usando

### Ver Créditos Restantes

1. En AWS Academy Learner Lab, mira el panel superior
2. Aparece: "Budget: $XX.XX of $100.00 used"

### Buenas Prácticas para Ahorrar

- **Comprime imágenes:** Usa JPG con 70-80% de calidad
- **Limita tamaño de MP3:** Máximo 10MB por canción
- **Elimina archivos de prueba:** Cuando termines el proyecto
- **No dejes EC2 corriendo:** Si usas EC2, detenlo al terminar
- **Monitorea diariamente:** Revisa el presupuesto usado

---

## 📚 Recursos Adicionales

### Documentación del Proyecto
- [README.md](README.md) - Guía de inicio rápido
- [API_DOCUMENTATION.md](API_DOCUMENTATION.md) - Documentación técnica
- [ENDPOINTS.md](ENDPOINTS.md) - Lista de endpoints
- [ARCHITECTURE.md](ARCHITECTURE.md) - Arquitectura del proyecto
- [VER_TABLAS_POSTGRES.md](VER_TABLAS_POSTGRES.md) - Configurar PostgreSQL
- [JWT_TESTING_GUIDE.md](JWT_TESTING_GUIDE.md) - Testing con JWT

### AWS
- [AWS S3 Documentation](https://docs.aws.amazon.com/s3/)
- [AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/)
- [AWS Academy Student Guide](https://awsacademy.instructure.com/)

### Ktor
- [Ktor Documentation](https://ktor.io/docs/)
- [Ktor Multipart Support](https://ktor.io/docs/multipart-support.html)
- [Ktor File Upload](https://ktor.io/docs/uploads.html)

---

## ✅ Resumen

**¡Felicidades!** Ahora tu API puede:

✅ Subir imágenes de artistas a S3 automáticamente  
✅ Subir portadas de álbumes a S3  
✅ Subir archivos MP3 de canciones a S3  
✅ Generar URLs firmadas temporales (12 horas)  
✅ Solo guardar la KEY del archivo en PostgreSQL  
✅ Servir URLs diferentes cada vez que se consulte  
✅ Funcionar con credenciales temporales de AWS Academy  

---

**Autor:** Luis Alberto Nafate Hernández  
**Proyecto:** Music Player API  
**Fecha:** Noviembre 2025  
**Institución:** Universidad Politécnica de Chiapas  
