Restaurante Catalogo API
Endpoints disponibles
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

Estructura del proyecto

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

