# Sistema de Roles y Navegación - HospedaYa Frontend

## Descripción General

El frontend de HospedaYa está organizado con un **sistema de roles basado en guards** que controla el acceso a diferentes páginas según el tipo de usuario:

- **HUESPED** (Huésped): Usuarios que buscan y reservan alojamientos
- **ANFITRION** (Anfitrión): Usuarios que publican y gestionan alojamientos

## Estructura de Rutas

### Rutas Públicas (sin autenticación)
- `/` - Landing page
- `/login` - Inicio de sesión
- `/register` - Registro de huéspedes
- `/register-host` - Registro de anfitriones
- `/recuperar-password` - Recuperación de contraseña
- `/reset-password` - Restablecer contraseña

### Rutas para HUÉSPEDES (rol: HUESPED)
- `/dashboard` - Dashboard principal del huésped
- `/resultados` - Búsqueda de alojamientos (también accesible sin login)
- `/alojamientos/:id/reservar` - Realizar reserva de un alojamiento
- `/reservas` - Ver mis reservas

### Rutas para ANFITRIONES (rol: ANFITRION)
- `/dashboard-anfitrion` - Dashboard del anfitrión con KPIs
- `/alojamientos/gestion` - Gestión de alojamientos publicados
- `/alojamientos/nuevo` - Crear nuevo alojamiento

### Rutas Compartidas (requiere autenticación)
- `/profile-setup` - Configuración del perfil de usuario

## Guards de Seguridad

### `authGuard`
Verifica que el usuario esté autenticado. Si no lo está, redirige a `/login`.

**Uso**: Protege rutas que requieren autenticación sin importar el rol.

### `userGuard` 
Verifica que el usuario sea un HUÉSPED autenticado. 
- Si no está autenticado → redirige a `/login`
- Si es ANFITRION → redirige a `/dashboard-anfitrion`

**Uso**: Protege rutas exclusivas para huéspedes.

### `hostGuard`
Verifica que el usuario sea un ANFITRIÓN autenticado.
- Si no está autenticado → redirige a `/login`
- Si es HUESPED → redirige a `/dashboard`

**Uso**: Protege rutas exclusivas para anfitriones.

### `guestOnlyGuard`
Permite acceso anónimo o a huéspedes logueados.
- Si es ANFITRION logueado → redirige a `/dashboard-anfitrion`

**Uso**: Para páginas como búsqueda que pueden accederse sin login pero restringen anfitriones.

## Componentes Compartidos

### Header Component
Ubicación: `src/app/shared/components/header/`

El header se adapta dinámicamente según el estado de autenticación y rol del usuario:

#### Usuario NO autenticado
- Logo HospedaYa
- Links: Inicio, Buscar alojamientos
- Botones: Iniciar Sesión, Registrarse

#### Usuario HUÉSPED autenticado
- Logo HospedaYa
- Links: Inicio, Buscar, Mis Reservas
- Menú de perfil con avatar

#### Usuario ANFITRIÓN autenticado
- Logo HospedaYa
- Links: Dashboard, Mis Alojamientos, + Crear alojamiento
- Menú de perfil con avatar

## Flujo de Autenticación

1. **Login** (`/login`)
   - El usuario ingresa email y contraseña
   - El backend retorna un JWT token
   - Se guarda el token en localStorage
   - Se carga el perfil del usuario (`/usuarios/me`)
   - Se guarda el perfil en localStorage
   - Redirige según el rol:
     - ANFITRION → `/dashboard-anfitrion`
     - HUESPED → `/dashboard`

2. **Registro**
   - Huéspedes: `/register` → Registra con rol HUESPED
   - Anfitriones: `/register-host` → Registra con rol ANFITRION

3. **Logout**
   - Elimina el token y datos del usuario de localStorage
   - Redirige a landing page (`/`)

## Servicios

### AuthService
`src/app/services/auth.service.ts`

- `login()` - Autenticar usuario
- `register()` - Registrar nuevo usuario
- `getToken()` - Obtener JWT token
- `setToken()` - Guardar JWT token
- `getUser()` - Obtener datos del usuario actual
- `saveUser()` - Guardar datos del usuario
- `logout()` - Cerrar sesión
- `isLoggedIn()` - Verificar si hay sesión activa

### UsuarioService
`src/app/services/usuario.service.ts`

- `me()` - Obtener perfil del usuario actual
- `update()` - Actualizar perfil
- `uploadFoto()` - Subir foto de perfil

### AlojamientoService
`src/app/services/alojamiento.service.ts`

- `listar()` - Listar todos los alojamientos
- `listarPorAnfitrion()` - Listar alojamientos de un anfitrión
- `obtener()` - Obtener detalles de un alojamiento
- `crearAlojamiento()` - Crear nuevo alojamiento

### ReservaService
`src/app/services/reserva.service.ts`

- `crearReserva()` - Crear nueva reserva
- `porUsuario()` - Listar reservas de un usuario

## Interceptores

### AuthInterceptor
`src/app/services/auth.interceptor.ts`

Automáticamente adjunta el JWT token a todas las peticiones HTTP hacia el backend:
- Lee el token de localStorage
- Lo agrega en el header `Authorization: Bearer <token>`

## Iniciar el Frontend

```bash
cd hospedaya-frontend
npm install
npm start
```

El servidor de desarrollo estará en `http://localhost:4200`

## Requisitos

- Node.js 18+
- Angular CLI 20.2
- Backend corriendo en `http://localhost:8080`

## Variables de Configuración

Los servicios apuntan por defecto a:
- Backend API: `http://localhost:8080`
- Auth endpoint: `http://localhost:8080/auth`
- Usuarios endpoint: `http://localhost:8080/usuarios`
- Alojamientos endpoint: `http://localhost:8080/alojamientos`
- Reservas endpoint: `http://localhost:8080/reservas`

## Próximos Pasos / Mejoras

- [ ] Agregar funcionalidad de favoritos
- [ ] Implementar notificaciones en tiempo real
- [ ] Agregar filtros avanzados de búsqueda
- [ ] Implementar chat entre huésped y anfitrión
- [ ] Agregar sistema de comentarios y calificaciones
- [ ] Implementar panel de administración (rol ADMIN)
