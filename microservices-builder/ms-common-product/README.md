# ms-common-product

Microservicio de productos con arquitectura hexagonal (puertos y adaptadores).

## Capas y relaciones

| Capa | Rol | Se conecta con |
|------|-----|----------------|
| **domain** | Núcleo: `Product` sin dependencias externas | Tipos usados por application e infrastructure |
| **application** | Casos de uso: `ProductUseCase`, `ProductService`, `ApplicationResult` | Puertos in/out; no conoce HTTP ni BD |
| **infrastructure.web** | Adaptador primario (driving): frontera HTTP | Invoca `ProductUseCase`; traduce con `ProductRestMapper` y `ResultHttpMapper` |
| **infrastructure.persistence** | Adaptador secundario (driven): persistencia | Implementa `ProductPersistencePort`; oculta `ProductEntity` y repositorio |

Flujo de una petición:

```
Cliente HTTP
  → ProductRestController (adaptador web)
  → ProductRestMapper: DTO → Product
  → ProductUseCase (ProductService)
  → ProductPersistencePort (JpaProductPersistenceAdapter)
  → ProductPersistenceMapper: Product ↔ ProductEntity
  ← ApplicationResult<Product>
  ← ResultHttpMapper: ApplicationResult → DTO + status HTTP
```

Errores de negocio: `ProductService` devuelve `ApplicationResult.fail(ApplicationError)` → `ResultHttpMapper` → `DErrorResponse`.  
Errores técnicos en la frontera HTTP: `GlobalExceptionHandler` construye `ApplicationError` y reutiliza `ResultHttpMapper`.

## Estructura

```
com.practice
├── domain/model/Product.java
├── application
│   ├── port/in/ProductUseCase.java
│   ├── port/out/ProductPersistencePort.java
│   ├── result/ApplicationError, ApplicationResult, EErrorType
│   └── service/ProductService.java
└── infrastructure
    ├── persistence/adapter, entity, mapper, repository
    └── web
        ├── controller/ProductRestController.java
        ├── dto/DCreate…, DUpdate…, DProduct…, DErrorResponse
        ├── mapper/ProductRestMapper.java
        ├── result/ResultHttpMapper.java
        └── GlobalExceptionHandler.java
```

## Mappers MapStruct

| Interfaz | Frontera | Función |
|----------|----------|---------|
| `ProductPersistenceMapper` | Adaptador de persistencia | `Product` ↔ `ProductEntity` |
| `ProductRestMapper` | Adaptador web | DTOs HTTP ↔ `Product` / `DProductResponse` |

`ResultHttpMapper` es el puente application ↔ frontera HTTP: construye la respuesta y proyecta `ApplicationError` → `DErrorResponse` (sin MapStruct).

## Ejecutar

```bash
mvn clean install -pl ms-common-product
mvn spring-boot:run -pl ms-common-product
```

Swagger: http://localhost:8081/swagger-ui.html
