# 🏠 HospedaYa - Plataforma de Reserva de Alojamientos

![Estado](https://img.shields.io/badge/Estado-PRODUCCIÓN-success)
![Backend](https://img.shields.io/badge/Backend-Spring%20Boot%203.5.6-green)
![Frontend](https://img.shields.io/badge/Frontend-Angular%2020.2-red)
![Base de Datos](https://img.shields.io/badge/BD-PostgreSQL-blue)

Sistema completo de reserva de alojamientos similar a Airbnb, con gestión diferenciada para huéspedes y anfitriones.

---

## 📋 Inicio Rápido

### Prerequisitos
- ☕ Java 21
- 🗄️ PostgreSQL 14+
- 📦 Node.js 18+
- 🔧 Gradle (incluido)

### Instalación y Ejecución

```bash
# 1. Iniciar PostgreSQL
brew services start postgresql@14

# 2. Crear base de datos (solo primera vez)
psql -U postgres
CREATE DATABASE hospedaya;
CREATE USER hospedayaadmin WITH PASSWORD 'Hospeday@';
GRANT ALL PRIVILEGES ON DATABASE hospedaya TO hospedayaadmin;
\q

# 3. Iniciar Backend
cd backend
./gradlew bootRun

# 4. Iniciar Frontend (en otra terminal)
cd hospedaya-frontend
npm install  # Solo primera vez
npm start
```

### Acceder a la Aplicación

- **Frontend:** http://localhost:4200
- **Backend API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html

---

## 🔐 Credenciales de Prueba

El sistema crea automáticamente estos usuarios al iniciar:

| Rol | Email | Contraseña | Acceso |
|-----|-------|------------|--------|
| 👤 **Huésped** | huesped@test.com | 123456 | Búsqueda y reserva de alojamientos |
| 🏠 **Anfitrión** | anfitrion@test.com | 123456 | Gestión de alojamientos propios |
| 👨‍💼 **Admin** | admin@test.com | admin123 | Panel de administración |

---

## 📚 Documentación

### 🚀 Para Producción
- **[PRODUCCION.md](./PRODUCCION.md)** - Guía completa de configuración de producción
  - Credenciales de prueba detalladas
  - Configuración de PostgreSQL
  - Solución de problemas
  - Testing de endpoints

- **[CAMBIOS-PRODUCCION.md](./CAMBIOS-PRODUCCION.md)** - Resumen de cambios implementados
  - Inicialización automática de usuarios
  - Arquitectura de seguridad
  - Flujo de autenticación
  - Verificación del sistema

### ⚙️ Configuración Técnica
- **[WARP.md](./WARP.md)** - Comandos de desarrollo y arquitectura del proyecto
  - Estructura del proyecto
  - Comandos de desarrollo
  - Arquitectura backend y frontend
  - Stack tecnológico

- **[backend/JAVA-SETUP.md](./backend/JAVA-SETUP.md)** - Configuración de Java 21
  - Instalación de Java 21
  - Configuración de JAVA_HOME
  - Soluciones alternativas

### 🎨 Frontend
- **[hospedaya-frontend/ROLES-SISTEMA.md](./hospedaya-frontend/ROLES-SISTEMA.md)** - Sistema de roles y navegación
  - Guards de seguridad
  - Rutas protegidas por rol
  - Componentes compartidos
  - Servicios HTTP

- **[hospedaya-frontend/VERIFICACION.md](./hospedaya-frontend/VERIFICACION.md)** - Guía de verificación
  - Checklist de funcionalidad
  - Flujos de navegación
  - Troubleshooting
  - Testing

---

## 🏗️ Arquitectura del Proyecto

### Backend (Spring Boot)

```
backend/
├── presentation/         # Controllers (REST endpoints)
├── application/          # Services, DTOs, Mappers
├── domain/              # Entities, Enums
└── infraestructure/     # Repositories, Security, Config
```

**Capas:**
- **Presentation:** Controladores REST con OpenAPI/Swagger
- **Application:** Lógica de negocio y DTOs
- **Domain:** Modelo de dominio (Entidades JPA)
- **Infrastructure:** Repositorios, Seguridad JWT, Configuración

### Frontend (Angular Standalone)

```
hospedaya-frontend/src/app/
├── pages/               # Componentes de páginas
├── services/            # Servicios HTTP y auth
├── shared/              # Componentes compartidos (header)
└── app.routes.ts        # Rutas con guards
```

**Características:**
- Componentes standalone (Angular 20)
- Guards basados en roles
- Interceptor HTTP para JWT
- Header dinámico según rol

---

## 🎯 Funcionalidades Principales

### Para Huéspedes (HUESPED)
- 🔍 Búsqueda de alojamientos
- 📅 Realizar reservas
- 📋 Ver mis reservas
- ⭐ Gestionar favoritos
- 💬 Dejar comentarios

### Para Anfitriones (ANFITRION)
- ➕ Crear alojamientos
- ✏️ Editar alojamientos
- 📊 Dashboard con KPIs
- 📥 Ver reservas recibidas
- 📈 Estadísticas de ocupación

### Para Administradores (ADMIN)
- 👥 Gestión de usuarios
- 🏘️ Gestión de alojamientos
- 💰 Gestión de pagos
- 📊 Reportes y estadísticas

---

## 🛠️ Stack Tecnológico

### Backend
- **Framework:** Spring Boot 3.5.6
- **Lenguaje:** Java 21
- **Base de Datos:** PostgreSQL (prod) / H2 (dev)
- **Seguridad:** Spring Security + JWT
- **Encriptación:** BCrypt
- **Mapeo:** MapStruct 1.6.2
- **Documentación:** SpringDoc OpenAPI 2.8.12
- **Pagos:** Mercado Pago SDK 2.1.27
- **Email:** Spring Mail

### Frontend
- **Framework:** Angular 20.2
- **Lenguaje:** TypeScript 5.9
- **Componentes:** Standalone (sin NgModules)
- **HTTP:** HttpClient con interceptores
- **Routing:** Angular Router con guards
- **Testing:** Karma + Jasmine

### Base de Datos
- **Producción:** PostgreSQL 14+
- **Desarrollo:** H2 in-memory
- **ORM:** Spring Data JPA / Hibernate

---

## 🔒 Seguridad

### Autenticación
- JWT (JSON Web Tokens)
- BCrypt para encriptación de contraseñas
- Tokens con expiración
- Refresh tokens (futuro)

### Autorización
- Roles: HUESPED, ANFITRION, ADMIN
- Guards en frontend para protección de rutas
- Interceptor HTTP para adjuntar tokens
- Validación de roles en backend

### CORS
- Configurado para `http://localhost:4200`
- Métodos permitidos: GET, POST, PUT, DELETE, OPTIONS

---

## 💳 Integración de Pagos

- **Proveedor:** Mercado Pago
- **Funcionalidad:** Procesamiento de pagos de reservas
- **Webhooks:** Notificaciones de cambios de estado
- **Testing:** Modo sandbox con credenciales TEST-*

---

## 🧪 Testing

### Backend
```bash
cd backend
./gradlew test
```

### Frontend
```bash
cd hospedaya-frontend
npm test
```

---

## 📊 Modelo de Datos

### Entidades Principales
- **Usuario:** Información de usuarios (huéspedes/anfitriones/admins)
- **Alojamiento:** Propiedades publicadas por anfitriones
- **Reserva:** Reservas realizadas por huéspedes
- **Pago:** Pagos asociados a reservas
- **Comentario:** Reseñas de huéspedes sobre alojamientos
- **Favorito:** Alojamientos guardados por usuarios
- **Servicio:** Amenidades de alojamientos (WiFi, piscina, etc.)

### Relaciones
- Usuario (1) → (N) Alojamiento (como anfitrión)
- Usuario (1) → (N) Reserva (como huésped)
- Alojamiento (1) → (N) Reserva
- Reserva (1) → (1) Pago
- Alojamiento (N) ↔ (N) Servicio

---

## 🚀 Deployment

### Backend (Producción)
```bash
cd backend
./gradlew build
java -jar build/libs/backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Frontend (Producción)
```bash
cd hospedaya-frontend
npm run build
# Archivos generados en dist/
```

---

## 📝 Variables de Entorno

### Backend
```bash
# Mercado Pago
export MP_ACCESS_TOKEN=TEST-xxx  # o APP_USR-xxx para prod
export MP_SUCCESS_URL=https://tu-dominio.com/retorno/mp/success
export MP_PENDING_URL=https://tu-dominio.com/retorno/mp/pending
export MP_FAILURE_URL=https://tu-dominio.com/retorno/mp/failure
export MP_WEBHOOK_URL=https://tu-dominio.com/webhooks/mercadopago

# Email
export MAIL_USERNAME=tu-correo@gmail.com
export MAIL_PASSWORD=tu-contraseña-de-aplicación
```

---

## 🐛 Solución de Problemas

### "Credenciales inválidas"
✅ Verifica que PostgreSQL esté corriendo
✅ Los usuarios se crean automáticamente al iniciar

### "Could not connect to database"
✅ Inicia PostgreSQL: `brew services start postgresql@14`
✅ Verifica que la base de datos exista

### "JAVA_HOME is set to an invalid directory"
✅ Consulta `backend/JAVA-SETUP.md` para configurar Java 21

### Ver más soluciones
📖 Consulta [PRODUCCION.md](./PRODUCCION.md) para más detalles

---

## 📈 Estado del Proyecto

- ✅ Backend funcional con Spring Boot
- ✅ Frontend funcional con Angular
- ✅ Sistema de autenticación JWT
- ✅ Sistema de roles (Huésped/Anfitrión/Admin)
- ✅ Guards de navegación
- ✅ Base de datos PostgreSQL persistente
- ✅ Usuarios de prueba automáticos
- ✅ Integración con Mercado Pago
- ✅ Sistema de emails
- ✅ Documentación completa

---

## 🤝 Contribución

Este proyecto fue desarrollado como parte del curso de Programación Avanzada.

---

## 📄 Licencia

Este proyecto es de uso académico.

---

## 📞 Soporte

Para problemas o dudas:
1. Revisa la documentación en los archivos `.md`
2. Verifica los logs del backend y frontend
3. Consulta el archivo de troubleshooting correspondiente

---

**Última actualización:** 2025-11-10

**Estado:** ✅ LISTO PARA PRODUCCIÓN
