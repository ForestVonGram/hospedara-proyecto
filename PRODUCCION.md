# 🚀 Configuración de Producción - HospedaYa

## ✨ Estado del Proyecto

El proyecto está **LISTO PARA PRODUCCIÓN** con:
- ✅ Usuarios de prueba pre-configurados
- ✅ Contraseñas encriptadas con BCrypt
- ✅ Base de datos PostgreSQL persistente
- ✅ Inicialización automática de datos
- ✅ Sistema de roles funcional

---

## 🔐 Credenciales de Prueba

El sistema crea automáticamente estos usuarios al iniciar:

### 👤 Huésped
- **Email:** `huesped@test.com`
- **Contraseña:** `123456`
- **Rol:** HUESPED
- **Acceso:** Búsqueda y reserva de alojamientos

### 🏠 Anfitrión
- **Email:** `anfitrion@test.com`
- **Contraseña:** `123456`
- **Rol:** ANFITRION
- **Acceso:** Gestión de alojamientos propios

### 👨‍💼 Administrador
- **Email:** `admin@test.com`
- **Contraseña:** `admin123`
- **Rol:** ADMIN
- **Acceso:** Panel de administración (futuro)

---

## 🗄️ Base de Datos PostgreSQL

### Configuración Actual

```properties
URL: jdbc:postgresql://localhost:5432/hospedaya
Usuario: hospedayaadmin
Contraseña: Hospeday@
```

### Verificar que PostgreSQL esté corriendo

```bash
# macOS con Homebrew
brew services list | grep postgresql

# Si no está corriendo, iniciarlo
brew services start postgresql@14

# O manualmente
pg_ctl -D /usr/local/var/postgres start
```

### Conectarse a PostgreSQL

```bash
psql -U hospedayaadmin -d hospedaya

# Dentro de psql:
\dt                    # Listar tablas
SELECT * FROM usuario; # Ver usuarios
\q                     # Salir
```

### Crear la base de datos (solo primera vez)

Si la base de datos no existe, créala:

```bash
# Conectarse como superusuario
psql -U postgres

# Dentro de psql:
CREATE DATABASE hospedaya;
CREATE USER hospedayaadmin WITH PASSWORD 'Hospeday@';
GRANT ALL PRIVILEGES ON DATABASE hospedaya TO hospedayaadmin;
\q
```

---

## 🚀 Iniciar el Proyecto

### 1. Iniciar PostgreSQL

```bash
# Verificar que esté corriendo
brew services list | grep postgresql

# Si no está corriendo
brew services start postgresql@14
```

### 2. Iniciar Backend

```bash
cd backend
./gradlew bootRun
```

Al iniciar, verás en la consola:

```
🔍 Verificando usuarios de prueba...
✅ Usuario huésped creado - Email: huesped@test.com | Password: 123456
✅ Usuario anfitrión creado - Email: anfitrion@test.com | Password: 123456
✅ Usuario admin creado - Email: admin@test.com | Password: admin123
✨ Inicialización de datos completada
═══════════════════════════════════════════════════════
📋 CREDENCIALES DE PRUEBA:
   HUÉSPED:   huesped@test.com   / 123456
   ANFITRIÓN: anfitrion@test.com / 123456
   ADMIN:     admin@test.com     / admin123
═══════════════════════════════════════════════════════
```

### 3. Iniciar Frontend

```bash
cd hospedaya-frontend
npm start
```

---

## 🔒 Seguridad de Contraseñas

### Cómo funciona

1. **Registro/Creación:** 
   - La contraseña en texto plano se encripta con BCrypt
   - Se guarda el hash en la base de datos
   - Ejemplo: `123456` → `$2a$10$xN9LxX...` (60 caracteres)

2. **Login:**
   - El usuario envía la contraseña en texto plano
   - Spring Security compara usando BCrypt
   - Si coincide → JWT token generado
   - Si no coincide → Error 401

3. **Persistencia:**
   - Los hashes se mantienen en PostgreSQL
   - **NO** se re-encriptan al reiniciar
   - Los usuarios persisten entre reinicios

### Verificar contraseña encriptada

```bash
psql -U hospedayaadmin -d hospedaya

SELECT email, password FROM usuario WHERE email = 'huesped@test.com';
```

Deberías ver algo como:
```
       email        |                           password                           
--------------------+--------------------------------------------------------------
 huesped@test.com   | $2a$10$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ
```

---

## 🔧 Solución de Problemas

### Problema: "Credenciales inválidas" después de reiniciar

**Causa:** La base de datos PostgreSQL se reinició y los datos se perdieron.

**Solución:**
1. Verificar que PostgreSQL esté corriendo:
   ```bash
   brew services list | grep postgresql
   ```

2. Verificar que la base de datos exista:
   ```bash
   psql -U postgres -l | grep hospedaya
   ```

3. Si no existe, crearla (ver sección "Crear la base de datos")

4. Reiniciar el backend - los usuarios se crearán automáticamente

### Problema: "Could not connect to database"

**Causa:** PostgreSQL no está corriendo.

**Solución:**
```bash
brew services start postgresql@14
```

### Problema: Los usuarios ya existen pero no puedo iniciar sesión

**Causa:** Puede haber un problema con el hash de la contraseña.

**Solución:**
1. Eliminar los usuarios existentes:
   ```bash
   psql -U hospedayaadmin -d hospedaya
   DELETE FROM usuario WHERE email IN ('huesped@test.com', 'anfitrion@test.com', 'admin@test.com');
   \q
   ```

2. Reiniciar el backend - se crearán de nuevo con contraseñas correctas

### Problema: "Role 'hospedayaadmin' does not exist"

**Causa:** El usuario de PostgreSQL no fue creado.

**Solución:**
```bash
psql -U postgres
CREATE USER hospedayaadmin WITH PASSWORD 'Hospeday@';
GRANT ALL PRIVILEGES ON DATABASE hospedaya TO hospedayaadmin;
\q
```

---

## 🧪 Testing

### Probar Login de Huésped

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "huesped@test.com",
    "password": "123456"
  }'
```

Respuesta esperada:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Probar Login de Anfitrión

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "anfitrion@test.com",
    "password": "123456"
  }'
```

### Probar Registro de Nuevo Usuario

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Nuevo Usuario",
    "email": "nuevo@test.com",
    "password": "password123",
    "rol": "HUESPED"
  }'
```

---

## 📝 Configuración de Hibernate

El proyecto usa `spring.jpa.hibernate.ddl-auto=update` que:
- ✅ Mantiene las tablas existentes
- ✅ Actualiza el esquema si cambias entidades
- ✅ **NO borra datos** al reiniciar
- ✅ Persiste datos entre reinicios

---

## 🔄 Flujo de Inicialización

1. **Backend inicia**
2. **Hibernate crea/actualiza tablas** (si es necesario)
3. **DataInitializer se ejecuta**
4. **Verifica si existen usuarios de prueba**
5. **Si NO existen → los crea con contraseñas encriptadas**
6. **Si YA existen → los deja intactos**
7. **Muestra credenciales en consola**

---

## 🎯 Checklist de Producción

- [x] Base de datos PostgreSQL persistente
- [x] Usuarios de prueba automáticos
- [x] Contraseñas encriptadas con BCrypt
- [x] JWT para autenticación
- [x] Guards de roles en frontend
- [x] Interceptor HTTP para token
- [x] CORS configurado
- [x] Manejo de errores
- [x] Logging configurado
- [x] Documentación completa

---

## 📚 Referencias

- **Backend:** Spring Boot 3.5.6 + PostgreSQL
- **Frontend:** Angular 20.2
- **Seguridad:** Spring Security + JWT
- **Encriptación:** BCrypt
- **Base de datos:** PostgreSQL 14+

---

## 📞 Soporte

Si tienes problemas:
1. Revisa los logs del backend
2. Verifica que PostgreSQL esté corriendo
3. Confirma que la base de datos existe
4. Intenta eliminar y recrear usuarios de prueba
5. Verifica las credenciales en la consola al iniciar

---

**Estado:** ✅ LISTO PARA PRODUCCIÓN

**Última actualización:** 2025-11-10
