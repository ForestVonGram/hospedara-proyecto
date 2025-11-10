# ☁️ Configuración de Cloudinary para HospedaYa

## 📋 Descripción

Cloudinary es un servicio de almacenamiento y gestión de imágenes en la nube que permite:
- ✅ Subir imágenes desde el dispositivo
- ✅ Optimización automática de imágenes
- ✅ Transformaciones de imágenes (redimensionar, recortar, etc.)
- ✅ CDN global para carga rápida
- ✅ Almacenamiento ilimitado en plan gratuito (hasta cierto límite de uso)

---

## 🚀 Paso 1: Crear Cuenta en Cloudinary

1. Ve a [https://cloudinary.com](https://cloudinary.com)
2. Haz clic en **"Sign Up for Free"**
3. Completa el registro con tu email
4. Verifica tu email

---

## 🔑 Paso 2: Obtener Credenciales

Una vez en tu dashboard de Cloudinary:

1. Ve a **Dashboard** → [https://cloudinary.com/console](https://cloudinary.com/console)
2. Verás algo como esto:

```
Cloud name:    tu-nombre-cloud
API Key:       123456789012345
API Secret:    abcdefghijklmnopqrstuvwxyz123
```

3. **Copia estas tres credenciales** (las necesitarás en el siguiente paso)

---

## ⚙️ Paso 3: Configurar Variables de Entorno

### Opción 1: Variables de entorno del sistema (Recomendado)

En tu terminal (macOS/Linux):

```bash
# Agregar a ~/.zshrc o ~/.bash_profile
export CLOUDINARY_CLOUD_NAME="tu-nombre-cloud"
export CLOUDINARY_API_KEY="123456789012345"
export CLOUDINARY_API_SECRET="abcdefghijklmnopqrstuvwxyz123"

# Recargar configuración
source ~/.zshrc
```

### Opción 2: Configuración directa en application.properties

Edita `backend/src/main/resources/application.properties`:

```properties
cloudinary.cloud-name=tu-nombre-cloud
cloudinary.api-key=123456789012345
cloudinary.api-secret=abcdefghijklmnopqrstuvwxyz123
```

⚠️ **IMPORTANTE:** Si usas esta opción, NO subas este archivo a Git con tus credenciales reales.

---

## 🧪 Paso 4: Verificar Configuración

1. **Inicia el backend:**
   ```bash
   cd backend
   ./gradlew bootRun
   ```

2. **Busca en los logs:**
   ```
   ✅ Cloudinary configurado para cloud: tu-nombre-cloud
   ```

3. Si ves este mensaje, ¡la configuración es correcta!

---

## 📤 Cómo Subir Imágenes

### Desde el Frontend

#### 1. Subir Avatar (Foto de Perfil)

```typescript
import { ImagenService } from './services/imagen.service';

// En tu componente
constructor(private imagenService: ImagenService) {}

onFileSelected(event: any) {
  const file: File = event.target.files[0];
  
  if (file) {
    this.imagenService.uploadAvatar(file).subscribe({
      next: (response) => {
        console.log('Avatar subido:', response.url);
        // Actualizar la UI con la nueva URL
      },
      error: (error) => {
        console.error('Error al subir avatar:', error);
      }
    });
  }
}
```

#### 2. Subir Imágenes de Alojamiento (Múltiples)

```typescript
onFilesSelected(event: any) {
  const files: File[] = Array.from(event.target.files);
  
  if (files.length > 0) {
    this.imagenService.uploadMultipleAlojamientoImages(files).subscribe({
      next: (response) => {
        console.log('Imágenes subidas:', response.urls);
        // response.urls contiene un array con las URLs de Cloudinary
      },
      error: (error) => {
        console.error('Error al subir imágenes:', error);
      }
    });
  }
}
```

### HTML para Input de Archivos

```html
<!-- Avatar (una sola imagen) -->
<input 
  type="file" 
  accept="image/*" 
  (change)="onFileSelected($event)"
/>

<!-- Alojamientos (múltiples imágenes) -->
<input 
  type="file" 
  accept="image/*" 
  multiple 
  (change)="onFilesSelected($event)"
/>
```

---

## 📊 Endpoints del Backend

### 1. Subir Avatar
```
POST /imagenes/avatar
Content-Type: multipart/form-data
Authorization: Bearer <token>

Body: 
  file: [archivo de imagen]

Response:
{
  "url": "https://res.cloudinary.com/...",
  "message": "Avatar actualizado exitosamente"
}
```

### 2. Subir Imagen de Alojamiento (Individual)
```
POST /imagenes/alojamiento
Content-Type: multipart/form-data
Authorization: Bearer <token>

Body:
  file: [archivo de imagen]

Response:
{
  "url": "https://res.cloudinary.com/...",
  "message": "Imagen subida exitosamente"
}
```

### 3. Subir Múltiples Imágenes de Alojamiento
```
POST /imagenes/alojamiento/multiple
Content-Type: multipart/form-data
Authorization: Bearer <token>

Body:
  files: [archivo1, archivo2, archivo3, ...]

Response:
{
  "urls": [
    "https://res.cloudinary.com/...",
    "https://res.cloudinary.com/...",
    "https://res.cloudinary.com/..."
  ],
  "count": 3,
  "message": "3 imágenes subidas exitosamente"
}
```

---

## 🔒 Validaciones Implementadas

### Avatar
- ✅ Tipo de archivo: Solo imágenes (jpg, png, gif, webp)
- ✅ Tamaño máximo: 5 MB
- ✅ Solo usuarios autenticados
- ✅ Elimina avatar anterior al subir uno nuevo

### Imágenes de Alojamiento
- ✅ Tipo de archivo: Solo imágenes
- ✅ Tamaño máximo: 10 MB por imagen
- ✅ Máximo 10 imágenes por alojamiento
- ✅ Solo usuarios autenticados

---

## 📁 Estructura en Cloudinary

Las imágenes se organizan en carpetas:

```
hospedaya/
├── avatars/          # Fotos de perfil de usuarios
│   ├── imagen1.jpg
│   ├── imagen2.jpg
│   └── ...
└── alojamientos/     # Fotos de alojamientos
    ├── imagen1.jpg
    ├── imagen2.jpg
    └── ...
```

---

## 🎨 Optimizaciones Automáticas

Cloudinary automáticamente:
- ✅ Convierte imágenes a JPG para optimización
- ✅ Aplica compresión inteligente (`quality: auto:good`)
- ✅ Entrega en formato WebP cuando el navegador lo soporta
- ✅ Usa CDN para carga rápida global

---

## 🔧 Solución de Problemas

### Error: "Cloudinary configuration not found"

**Causa:** No se configuraron las credenciales.

**Solución:**
1. Verifica que las variables de entorno estén configuradas
2. Reinicia el backend después de configurarlas
3. Verifica que no haya espacios en las credenciales

### Error: "File too large"

**Causa:** La imagen supera el tamaño máximo.

**Solución:**
- Avatar: Máximo 5 MB
- Alojamientos: Máximo 10 MB por imagen
- Comprime la imagen antes de subirla

### Error: "Invalid file type"

**Causa:** El archivo no es una imagen válida.

**Solución:**
- Solo se aceptan: JPG, JPEG, PNG, GIF, WEBP
- Verifica la extensión del archivo

### Error: "Unauthorized"

**Causa:** No estás autenticado o el token expiró.

**Solución:**
- Inicia sesión nuevamente
- Verifica que el token JWT se esté enviando en el header

---

## 📊 Límites del Plan Gratuito

Cloudinary Free Tier incluye:
- ✅ 25 créditos mensuales (25,000 transformaciones)
- ✅ 25 GB de almacenamiento
- ✅ 25 GB de ancho de banda
- ✅ Suficiente para desarrollo y proyectos pequeños

---

## 🧪 Testing

### Test con cURL

```bash
# Subir avatar (requiere token)
curl -X POST http://localhost:8080/imagenes/avatar \
  -H "Authorization: Bearer TU_TOKEN_JWT" \
  -F "file=@/ruta/a/tu/imagen.jpg"

# Subir imagen de alojamiento
curl -X POST http://localhost:8080/imagenes/alojamiento \
  -H "Authorization: Bearer TU_TOKEN_JWT" \
  -F "file=@/ruta/a/tu/imagen.jpg"

# Subir múltiples imágenes
curl -X POST http://localhost:8080/imagenes/alojamiento/multiple \
  -H "Authorization: Bearer TU_TOKEN_JWT" \
  -F "files=@/ruta/a/imagen1.jpg" \
  -F "files=@/ruta/a/imagen2.jpg"
```

---

## 📚 Recursos Adicionales

- **Dashboard:** https://cloudinary.com/console
- **Documentación:** https://cloudinary.com/documentation
- **API Reference:** https://cloudinary.com/documentation/image_upload_api_reference

---

## ✅ Checklist de Configuración

- [ ] Cuenta de Cloudinary creada
- [ ] Credenciales obtenidas (cloud_name, api_key, api_secret)
- [ ] Variables de entorno configuradas
- [ ] Backend reiniciado
- [ ] Log "✅ Cloudinary configurado" visible
- [ ] Test de subida de imagen exitoso

---

**Estado:** ✅ Cloudinary integrado y listo para usar

**Última actualización:** 2025-11-10
