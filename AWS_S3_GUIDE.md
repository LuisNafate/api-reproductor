# 🖼️ AWS S3 Image Upload - Guía Completa

## 📋 Sistema Implementado

El sistema funciona así:

1. **Subes la imagen** a S3 (vía Postman con multipart/form-data o URL pre-firmada)
2. **Guardas la URL** en la base de datos (en `image_url` o `cover_image_url`)
3. **Al consultar**, el JSON devuelve:
   - `image_url`: URL estándar de S3
   - `image_presigned_url`: URL pre-firmada (válida 24h) para visualización

## 🔧 Configuración

### 1. Variables de Entorno (.env)

```env
# AWS S3 Configuration
AWS_ACCESS_KEY=your-access-key-here
AWS_SECRET_KEY=your-secret-key-here
AWS_BUCKET=music-player-bucket
AWS_REGION=us-east-1
```

### 2. Obtener Credenciales AWS

**Opción A: AWS Console**
1. Ve a [AWS Console](https://console.aws.amazon.com/)
2. IAM → Users → Create User
3. Attach policies: `AmazonS3FullAccess`
4. Security credentials → Create access key
5. Copia `Access Key ID` y `Secret Access Key`

**Opción B: Usar LocalStack (desarrollo local)**
```bash
# Para testing sin AWS real
docker run -p 4566:4566 localstack/localstack
```

### 3. Crear Bucket S3

```bash
# AWS CLI
aws s3 mb s3://music-player-bucket --region us-east-1

# O en AWS Console:
# S3 → Create bucket → music-player-bucket
```

## 🚀 Endpoints Disponibles

### 1. Upload Imagen (Multipart)

```http
POST http://127.0.0.1:8080/api/v1/upload/image
Authorization: Bearer {{jwt_token}}
Content-Type: multipart/form-data

Form Data:
- file: [archivo de imagen]
- folder: artists (opcional, default: "uploads")
```

**Carpetas disponibles:**
- `artists` - Imágenes de artistas
- `albums` - Portadas de álbumes  
- `songs` - Covers de canciones
- `playlists` - Portadas de playlists
- `uploads` - Carpeta genérica

**Response 201:**
```json
{
  "file_name": "uuid-timestamp.jpg",
  "file_url": "https://music-player-bucket.s3.us-east-1.amazonaws.com/artists/uuid-timestamp.jpg",
  "presigned_url": "https://music-player-bucket.s3.us-east-1.amazonaws.com/artists/uuid-timestamp.jpg?X-Amz-...",
  "size_bytes": 245678,
  "content_type": "image/jpeg"
}
```

### 2. Generar URL Pre-firmada para Upload Directo

```http
POST http://127.0.0.1:8080/api/v1/upload/presigned-url
Authorization: Bearer {{jwt_token}}
Content-Type: application/json

{
  "file_name": "my-artist-photo.jpg",
  "content_type": "image/jpeg",
  "folder": "artists"
}
```

**Response 200:**
```json
{
  "upload_url": "https://music-player-bucket.s3.us-east-1.amazonaws.com/artists/uuid-timestamp.jpg?X-Amz-Algorithm=...",
  "file_key": "artists/uuid-timestamp.jpg",
  "expires_in_seconds": 3600
}
```

Luego el cliente hace:
```javascript
// PUT directo a la upload_url
fetch(uploadUrl, {
  method: 'PUT',
  body: fileBlob,
  headers: { 'Content-Type': 'image/jpeg' }
})
```

### 3. Eliminar Imagen (Solo ADMIN)

```http
DELETE http://127.0.0.1:8080/api/v1/upload/artists/uuid-timestamp.jpg
Authorization: Bearer {{jwt_token}}
```

## 📝 Flujo Completo en Postman

### Paso 1: Subir Imagen

1. **POST** `http://127.0.0.1:8080/api/v1/upload/image`
2. Headers:
   - `Authorization: Bearer {{jwt_token}}`
3. Body → form-data:
   - Key: `file`, Type: File, Value: [seleccionar imagen]
   - Key: `folder`, Type: Text, Value: `artists`
4. Send

### Paso 2: Copiar file_url del Response

```json
{
  "file_name": "abc123-1234567890.jpg",
  "file_url": "https://music-player-bucket.s3.us-east-1.amazonaws.com/artists/abc123-1234567890.jpg",
  "presigned_url": "https://music-player-bucket.s3.us-east-1.amazonaws.com/artists/abc123-1234567890.jpg?X-Amz-Algorithm=...",
  ...
}
```

### Paso 3: Crear Artista con la Imagen

```http
POST http://127.0.0.1:8080/api/v1/artists
Authorization: Bearer {{jwt_token}}
Content-Type: application/json

{
  "name": "Taylor Swift",
  "bio": "American singer-songwriter",
  "image_url": "https://music-player-bucket.s3.us-east-1.amazonaws.com/artists/abc123-1234567890.jpg"
}
```

### Paso 4: Consultar Artista (GET con URL pre-firmada)

```http
GET http://127.0.0.1:8080/api/v1/artists/1
```

**Response:**
```json
{
  "id": 1,
  "name": "Taylor Swift",
  "biography": "American singer-songwriter",
  "country": null,
  "image_url": "https://music-player-bucket.s3.us-east-1.amazonaws.com/artists/abc123-1234567890.jpg",
  "image_presigned_url": "https://music-player-bucket.s3.us-east-1.amazonaws.com/artists/abc123-1234567890.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=...",
  "created_at": "2025-11-28T14:30:00",
  "updated_at": "2025-11-28T14:30:00"
}
```

⚠️ **La `image_presigned_url` es válida por 24 horas** y permite acceso sin autenticación.

## 🔒 Seguridad

### Validaciones Implementadas

✅ **Requiere autenticación JWT** para upload  
✅ **Solo ADMIN** puede eliminar archivos  
✅ **Solo imágenes** permitidas (content-type `image/*`)  
✅ **Máximo 10MB** por archivo  
✅ **Nombres únicos** (UUID + timestamp)  
✅ **URLs pre-firmadas** con expiración  

### Permisos S3 Bucket

**CORS Configuration (en tu bucket S3):**
```json
[
  {
    "AllowedHeaders": ["*"],
    "AllowedMethods": ["GET", "PUT", "POST", "DELETE"],
    "AllowedOrigins": ["*"],
    "ExposeHeaders": ["ETag"]
  }
]
```

**Bucket Policy (público para GET):**
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PublicReadGetObject",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::music-player-bucket/*"
    }
  ]
}
```

## 🧪 Testing

### Test 1: Upload Imagen

```bash
# En Postman
POST http://127.0.0.1:8080/api/v1/upload/image
Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGc...

# Form-data
file: [seleccionar artist.jpg]
folder: artists
```

### Test 2: Verificar en S3

```bash
# AWS CLI
aws s3 ls s3://music-player-bucket/artists/

# Debería listar: abc123-1234567890.jpg
```

### Test 3: Acceso Directo

```bash
# Abrir en navegador (URL pre-firmada)
https://music-player-bucket.s3.us-east-1.amazonaws.com/artists/abc123-1234567890.jpg?X-Amz-...
```

## 📊 Estructura de Carpetas en S3

```
music-player-bucket/
├── artists/
│   ├── uuid1-timestamp.jpg
│   ├── uuid2-timestamp.png
│   └── ...
├── albums/
│   ├── uuid3-timestamp.jpg
│   └── ...
├── songs/
│   └── ...
├── playlists/
│   └── ...
└── uploads/
    └── ... (otros archivos)
```

## 🐛 Troubleshooting

### Error: AWS credentials not found
```
Solution: Verifica que .env tenga AWS_ACCESS_KEY y AWS_SECRET_KEY correctos
```

### Error: Bucket not found
```
Solution: 
1. Verifica que AWS_BUCKET existe en tu cuenta AWS
2. Verifica que AWS_REGION es correcto
```

### Error: Access Denied
```
Solution:
1. Verifica que tu usuario IAM tiene permisos S3
2. Verifica que el bucket policy permite las operaciones
```

### Imagen no carga en navegador
```
Solution:
1. Usa la image_presigned_url (no la image_url directa)
2. Verifica que la URL pre-firmada no expiró (24h)
3. Verifica CORS configuration en el bucket
```

## 📦 Dependencias Agregadas

```kotlin
// build.gradle.kts
implementation("aws.sdk.kotlin:s3:1.0.30")
implementation("aws.smithy.kotlin:http-client-engine-okhttp:1.0.10")
implementation("io.ktor:ktor-server-partial-content:$ktor_version")
```

## 🎯 Próximos Pasos

✅ JWT Authentication (COMPLETADO)  
✅ AWS S3 Integration (COMPLETADO)  
⏳ UUID Migration (pendiente)  

## 🔗 Referencias

- [AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/)
- [S3 Pre-signed URLs](https://docs.aws.amazon.com/AmazonS3/latest/userguide/PresignedUrlUploadObject.html)
- [Ktor Multipart](https://ktor.io/docs/multipart-support.html)
