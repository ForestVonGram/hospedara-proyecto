# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

HospedaYa is a full-stack accommodation booking platform (similar to Airbnb) consisting of:
- **Backend**: Spring Boot 3.5.6 REST API (Java 21) with PostgreSQL
- **Frontend**: Angular 20.2 standalone components application

## Development Commands

### Backend (Spring Boot)

Navigate to `backend/` directory for all backend commands.

**Build and Run:**
```bash
./gradlew build                                    # Build project
./gradlew bootRun                                  # Run with default profile (prod)
./gradlew bootRun --args='--spring.profiles.active=dev'  # Run with H2 in-memory DB
./gradlew bootRun --args='--spring.profiles.active=prod' # Run with PostgreSQL
```

**Testing:**
```bash
./gradlew test                                     # Run all tests
./gradlew test --tests AlojamientoServiceImplTest  # Run specific test class
./gradlew test --tests "*AlojamientoServiceImplTest.testCrearAlojamiento" # Run specific test method
```

**Other:**
```bash
./gradlew clean                                    # Clean build artifacts
./gradlew build --refresh-dependencies             # Refresh dependencies
```

### Frontend (Angular)

Navigate to `hospedaya-frontend/` directory for all frontend commands.

**Development:**
```bash
npm install                # Install dependencies
npm start                  # Start dev server (http://localhost:4200)
ng serve                   # Alternative to npm start
```

**Build:**
```bash
npm run build              # Production build (output in dist/)
ng build                   # Same as above
ng build --watch --configuration development  # Watch mode for development
```

**Testing:**
```bash
npm test                   # Run unit tests with Karma
ng test                    # Same as above
```

**Code Generation:**
```bash
ng generate component component-name           # Generate new component
ng generate service services/service-name      # Generate new service
ng generate --help                             # See all generation options
```

## Architecture

### Backend Architecture (Layered/Clean Architecture)

The backend follows a **4-layer architecture** with clear separation of concerns:

```
com.hospedaya.backend/
├── presentation/         # Controllers (REST endpoints)
├── application/          # Business logic, DTOs, Mappers, Services
├── domain/              # Entities, Enums (core domain model)
└── infraestructure/     # Repositories, Security, External integrations
```

**Layer Responsibilities:**

1. **Presentation Layer** (`presentation/controller/`):
   - REST controllers using Spring Web annotations
   - Request/response handling with DTOs
   - OpenAPI/Swagger documentation
   - Controllers: `AlojamientoController`, `UsuarioController`, `ReservaController`, `PagoController`, `AuthController`, `MercadoPagoWebhookController`, etc.

2. **Application Layer** (`application/`):
   - **DTOs** (`application/dto/`): Separate request/response/update DTOs per entity (e.g., `alojamiento/`, `usuario/`, `reserva/`)
   - **Mappers** (`application/mapper/`): MapStruct mappers for entity-DTO conversion
   - **Services** (`application/service/`):
     - `base/`: Service interfaces
     - `impl/`: Service implementations with business logic
     - `integration/`: External service integrations (e.g., `MercadoPagoService`)

3. **Domain Layer** (`domain/`):
   - **Entities** (`domain/entity/`): JPA entities representing core domain model
     - Core entities: `Usuario`, `Alojamiento`, `Reserva`, `Pago`, `Comentario`, `Favorito`, `Servicio`, `ImagenAlojamiento`, `Notificacion`, `TransaccionPago`, `AlojamientoServicio`
   - **Enums** (`domain/enums/`): `Rol`, `EstadoReserva`, `EstadoAlojamiento`, `EstadoPago`, etc.

4. **Infrastructure Layer** (`infraestructure/`):
   - **Repositories** (`infraestructure/repository/`): Spring Data JPA repositories
   - **Security** (`infraestructure/security/`): JWT authentication and authorization
     - `JwtProvider`, `JwtUtil`, `JwtFilter`, `JwtAuthenticationFilter`, `CustomUserDetailsService`
   - **Config** (`infraestructure/config/`): `SecurityConfig`, `CorsConfig`, `AppConfig`

**Key Domain Relationships:**
- `Usuario` (with `Rol` enum: HUESPED/ANFITRION/ADMIN) has many `Reserva`, `Comentario`, `Favorito`, `Notificacion`
- `Alojamiento` belongs to one `Usuario` (anfitrion), has many `Reserva`, `Comentario`, `ImagenAlojamiento`
- `Alojamiento` many-to-many with `Servicio` (through `AlojamientoServicio`)
- `Reserva` has one `Pago`, which can have multiple `TransaccionPago`

### Backend Configuration

**Profiles:**
- `dev`: H2 in-memory database (default for testing)
- `prod`: PostgreSQL database (default profile)

**Environment Variables Required:**
- `MP_ACCESS_TOKEN`: Mercado Pago access token (TEST- for dev, APP_USR- for prod)
- `MP_SUCCESS_URL`, `MP_PENDING_URL`, `MP_FAILURE_URL`: Mercado Pago redirect URLs
- `MP_WEBHOOK_URL`: Mercado Pago webhook endpoint URL

**Database (Production):**
- PostgreSQL on `localhost:5432`
- Database: `hospedaya`
- Username: `hospedayaadmin`
- Password: `Hospeday@`

### Frontend Architecture (Angular Standalone)

The frontend uses **Angular standalone components** (no NgModules):

```
src/app/
├── pages/           # Feature components (landing, login, register)
├── services/        # HTTP services and business logic (auth.service.ts)
├── app.routes.ts    # Route configuration
├── app.config.ts    # Application configuration
└── app.ts           # Root component
```

**Routing:**
- `/` → Landing page
- `/login` → Login page
- `/register` → Registration page

**Build Configuration:**
- Uses Angular 20.2 with standalone components
- Build output: `dist/` directory
- Dev server: `http://localhost:4200`
- Prettier configured with custom rules for Angular templates

### Technology Stack

**Backend:**
- Java 21
- Spring Boot 3.5.6 (Web, Data JPA, Security, Actuator)
- Spring Security with JWT (jjwt 0.11.5)
- PostgreSQL (production) / H2 (development)
- Lombok for boilerplate reduction
- MapStruct 1.6.2 for DTO mapping
- SpringDoc OpenAPI 2.8.12 for API documentation
- Mercado Pago SDK 2.1.27 for payment processing

**Frontend:**
- Angular 20.2 (standalone components)
- TypeScript 5.9
- RxJS 7.8
- Karma + Jasmine for testing

## Testing

### Backend Tests

Tests are organized in `backend/src/test/java/` mirroring the main structure:
- **Repository tests**: `infraestructure/repository/*RepositoryTest.java`
- **Service tests**: `application/service/impl/*ServiceImplTest.java`
- **Controller tests**: `presentation/controller/*ControllerTest.java`

Tests use JUnit 5 with Spring Boot Test support and H2 in-memory database.

### Frontend Tests

Unit tests use Karma + Jasmine test runner, located alongside component/service files with `.spec.ts` suffix.

## Payment Integration

The system integrates with **Mercado Pago** for payment processing:
- Payment creation via `PagoController` and `MercadoPagoService`
- Webhook handling in `MercadoPagoWebhookController` for payment status updates
- Transaction tracking via `TransaccionPago` entity
- Requires ngrok or similar for local webhook testing

## Security

- JWT-based authentication (tokens issued on login)
- Role-based authorization: `HUESPED`, `ANFITRION`, `ADMIN`
- JWT filters: `JwtFilter`, `JwtAuthenticationFilter`
- Password encryption handled by Spring Security
- CORS configuration in `CorsConfig`
