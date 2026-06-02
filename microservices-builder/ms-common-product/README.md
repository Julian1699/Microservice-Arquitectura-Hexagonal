# ms-common-product

Microservicio de productos con **CRUD**, **arquitectura hexagonal** y documentación **Swagger UI**.

## Stack

| Tecnología | Uso |
|------------|-----|
| Java 17 | Lenguaje |
| Spring Boot 3 | Framework |
| Spring Data JPA | Persistencia |
| PostgreSQL | Base de datos (runtime) |
| H2 | Tests de integración |
| springdoc-openapi | Swagger UI |
| JUnit 5 + Mockito | Tests unitarios |
| MapStruct | Mapper dominio ↔ entidad JPA |
| Lombok | Boilerplate |

## Convención de nombres

| Prefijo | Tipo | Ejemplo |
|---------|------|---------|
| `I` | Puerto de salida (interfaz) | `IProductPersistenceOutputPort` |
| `D` | DTO HTTP | `DCreateProductRequest` |
| *(sin prefijo)* | Dominio, servicios, adaptadores | `Product`, `ProductService` |

## Estructura

```
com.practice
├── domain/model/Product.java
├── application
│   ├── port/out/IProductPersistenceOutputPort.java
│   ├── service/ProductServiceUseCase.java
│   └── service/Impl/ProductService.java
└── infrastructure
    ├── controller/          # REST + DTOs + ProductRestMapper
    └── persistence/         # JPA + ProductPersistenceMapper
```

## Flujo

```
HTTP → ProductRestController → ProductServiceUseCase → ProductService
                                                      → IProductPersistenceOutputPort
                                                      → JpaProductPersistenceAdapter → PostgreSQL
```

## Endpoints

Base: `http://localhost:8081`

| Método | Ruta |
|--------|------|
| POST | `/api/product/create-product` |
| GET | `/api/product/find-product-by-id/{id}` |
| GET | `/api/product/find-all-products` |
| PUT | `/api/product/update-product/{id}` |
| DELETE | `/api/product/delete-product/{id}` |

## Ejecutar

1. Crear BD: `CREATE DATABASE ms_common_product;`
2. Ajustar `src/main/resources/application.yaml` si hace falta.
3. `mvn spring-boot:run` desde este módulo o `mvn spring-boot:run -pl ms-common-product` desde `microservices-builder`.

## Swagger UI

http://localhost:8081/swagger-ui.html

Configuración en `application.yaml` (`springdoc`). Los DTOs incluyen `@Schema` con ejemplos para Postman/Swagger.

## Postman

Importar: `postman/ms-common-product.postman_collection.json`

## Tests

```bash
mvn test
```

| Dependencia | Rol |
|-------------|-----|
| `spring-boot-starter-test` | Stack Spring Test |
| `junit-jupiter` | JUnit 5 |
| `mockito-core` / `mockito-junit-jupiter` | Mocks |
| `h2` | BD en memoria para `@SpringBootTest` |

Los tests unitarios de dominio/aplicación deben mockear `IProductPersistenceOutputPort` sin levantar PostgreSQL.

## Ejemplo JSON (crear / actualizar)

```json
{
  "name": "Teclado mecánico",
  "description": "Teclado RGB con switches rojos",
  "price": 250000,
  "stock": 8
}
```
