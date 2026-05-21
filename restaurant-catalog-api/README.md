# Restaurant Catalog API

Backend reactivo para catálogo de restaurante (hamburguesas, bebidas, postres).
Stack: Spring Boot 3.x · WebFlux · R2DBC · PostgreSQL · JWT

---

## Requisitos previos

- Java 17+
- Maven 3.9+
- PostgreSQL 14+ corriendo en local

---

## 1. Crear la base de datos

```sql
-- Conectarse a PostgreSQL como superusuario
CREATE DATABASE restaurant_db;
```

Luego ejecutar el schema:

```bash
psql -U postgres -d restaurant_db -f src/main/resources/schema.sql
```

---

## 2. Configurar application.yml

Editar `src/main/resources/application.yml` con tus credenciales reales:

```yaml
spring:
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/restaurant_db
    username: TU_USUARIO
    password: TU_PASSWORD

  mail:
    username: tu-email@gmail.com
    password: tu-app-password-de-gmail   # App Password, no la contraseña real

jwt:
  secret: "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
  expiration: 86400000   # 24h en milisegundos
```

> **Nota sobre el JWT secret:** debe tener al menos 32 bytes (256 bits) para HMAC-SHA256.
> Puedes generar uno con: `openssl rand -hex 32`

---

## 3. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

La API estará disponible en: `http://localhost:8080`

---

## 4. Endpoints disponibles

### Auth (público)
| Método | URL | Descripción |
|--------|-----|-------------|
| POST | `/api/auth/register` | Registrar nuevo usuario |
| POST | `/api/auth/login` | Iniciar sesión (devuelve JWT) |
| POST | `/api/auth/forgot-password` | Enviar enlace de recuperación |

### Categorías
| Método | URL | Auth | Descripción |
|--------|-----|------|-------------|
| GET | `/api/categories` | No | Listar todas |
| GET | `/api/categories/{id}` | No | Obtener por ID |
| POST | `/api/categories` | Si | Crear |
| PUT | `/api/categories/{id}` | Si | Actualizar |
| DELETE | `/api/categories/{id}` | Si | Eliminar |

### Productos
| Método | URL | Auth | Descripción |
|--------|-----|------|-------------|
| GET | `/api/products` | No | Listar (filtros: `?categoryId=1&available=true`) |
| GET | `/api/products/{id}` | No | Obtener por ID |
| POST | `/api/products` | Si | Crear |
| PUT | `/api/products/{id}` | Si | Actualizar |
| DELETE | `/api/products/{id}` | Si | Eliminar |

### Autenticación en requests protegidos
```
Authorization: Bearer <token>
```

---

## 5. Estructura del proyecto

```
src/main/java/com/restaurant/catalogapi/
├── config/
│   ├── CorsConfig.java              # CORS para localhost:3000
│   ├── R2dbcConfig.java             # Transacciones reactivas
│   └── SecurityConfig.java         # JWT filter + rutas públicas/privadas
├── controller/
│   ├── AuthController.java
│   ├── CategoryController.java
│   └── ProductController.java
├── dto/                             # Request y Response DTOs
├── exception/
│   ├── GlobalExceptionHandler.java  # @RestControllerAdvice
│   ├── DuplicateEmailException.java
│   ├── InvalidTokenException.java
│   └── ResourceNotFoundException.java
├── model/
│   ├── Category.java
│   ├── Product.java
│   └── User.java
├── repository/                      # ReactiveCrudRepository
├── security/
│   ├── JwtAuthFilter.java           # WebFilter JWT
│   ├── JwtProvider.java             # Generación/validación de tokens
│   └── ReactiveUserDetailsServiceImpl.java
├── service/
│   ├── AuthService.java
│   ├── CategoryService.java
│   ├── EmailService.java
│   └── ProductService.java
└── CatalogApiApplication.java
```

---

## 6. Próximos pasos

1. Implementar la lógica de negocio en los servicios (buscar los comentarios `// TODO`)
2. Conectar el frontend React (`http://localhost:3000`)
3. Agregar tests unitarios y de integración con `reactor-test` y `WebTestClient`
