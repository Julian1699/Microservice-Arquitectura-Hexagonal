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

## Pruebas

Hay **13 pruebas** en total. Cada una está numerada del **#1 al #13** en los comentarios del código (`PRUEBA #N de 13`) para localizarla rápido al leer o depurar.

Dentro de cada método los pasos van en **Given / When / Then** y se numeran (`1`, `2`, `3`…) para seguir qué hacer en cada fase.

### Catálogo de las 13 pruebas (lista completa)

| # | Tipo | Archivo | Método de test | Qué comprueba (en una frase) |
|---|------|---------|----------------|------------------------------|
| **1** | Smoke | `MsCommonProductApplicationTests` | `contextLoads` | Spring arranca y conecta beans (sin HTTP ni BD) |
| **2** | Unitaria | `ProductServiceTest` | `givenValidProduct_whenCreateProduct...` | Crear producto válido → ok y `save` |
| **3** | Unitaria | `ProductServiceTest` | `givenInvalidPrice_whenCreateProduct...` | Precio 0 → error, sin llamar a `save` |
| **4** | Unitaria | `ProductServiceTest` | `givenExistingId_whenGetProductById...` | Buscar por id que existe → ok |
| **5** | Unitaria | `ProductServiceTest` | `givenMissingId_whenGetProductById...` | Buscar id que no existe → `PRODUCT_NOT_FOUND` |
| **6** | Unitaria | `ProductServiceTest` | `givenProductsInPort_whenFindAllProducts...` | Listar productos → lista con 1 elemento |
| **7** | Unitaria | `ProductServiceTest` | `givenExistingProduct_whenUpdateProduct...` | Actualizar producto → ok y `save` |
| **8** | Unitaria | `ProductServiceTest` | `givenExistingProduct_whenDeleteProduct...` | Eliminar producto → ok y `deleteById` |
| **9** | Integración | `ProductRestControllerIntegrationTest` | `givenValidRequest_whenCreateProduct...` | `POST /create-product` → 201 y 1 fila en H2 |
| **10** | Integración | `ProductRestControllerIntegrationTest` | `givenExistingProduct_whenFindProductById...` | `GET /find-product-by-id/{id}` → 200 y JSON correcto |
| **11** | Integración | `ProductRestControllerIntegrationTest` | `givenExistingProduct_whenFindAllProducts...` | `GET /find-all-products` → 200 y array con 1 item |
| **12** | Integración | `ProductRestControllerIntegrationTest` | `givenExistingProduct_whenUpdateProduct...` | `PUT /update-product/{id}` → 200 y cambio en H2 |
| **13** | Integración | `ProductRestControllerIntegrationTest` | `givenExistingProduct_whenDeleteProduct...` | `DELETE /delete-product/{id}` → 204 y fila borrada |

**Resumen por tipo:** 1 smoke + 7 unitarias + 5 integración = **13**.

```
13 pruebas del módulo
│
├── #1  SMOKE      MsCommonProductApplicationTests     → ¿arranca Spring?
│
├── #2–#8  UNITARIAS   ProductServiceTest            → ¿lógica de ProductService? (mock del puerto)
│       #2 create ok
│       #3 create error precio
│       #4 get by id ok
│       #5 get by id no existe
│       #6 listar
│       #7 update
│       #8 delete
│
└── #9–#13  INTEGRACIÓN   ProductRestControllerIntegrationTest  → ¿HTTP + H2 de punta a punta?
        #9  POST create
        #10 GET by id
        #11 GET all
        #12 PUT update
        #13 DELETE
```

### Cómo leer el código de cada prueba

1. Abrir el archivo de la tabla (columna **Archivo**).
2. Leer el **Javadoc de la clase** (resumen del bloque #2–#8, #9–#13, etc.).
3. Revisar la **parte superior de la clase** (antes de los `@Test`): métodos como
   `validCreateRequest()`, `validUpdateRequest()`, `persistProduct()` o `validProductToCreate()`
   con los datos escritos dentro de cada método.
4. En cada `@Test`, leer el **Javadoc del método** y los pasos **Given / When / Then** numerados.

### Tipos de prueba (para quien empieza)

| Tipo | Clase | Qué se prueba | Qué es real | Qué se simula |
|------|-------|---------------|-------------|---------------|
| **Unitaria** | `ProductServiceTest` | Lógica de `ProductService` (casos de uso) | Solo la clase de application | `ProductPersistencePort` con Mockito |
| **Integración** | `ProductRestControllerIntegrationTest` | Endpoints HTTP del adaptador web | Controller, Service, Adapter, Repository, H2 | Nada del flujo principal |
| **Smoke** | `MsCommonProductApplicationTests` | Arranque del contexto Spring | Contexto completo en test | — |

La diferencia clave en arquitectura hexagonal:

- **Unitaria:** se aísla el núcleo de application; el puerto de salida no existe de verdad, solo se define su comportamiento esperado.
- **Integración:** se valida que los adaptadores (web y persistencia) encajan con application y que los datos llegan a la base de datos de prueba.

### Estructura de carpetas de test

```
src/test
├── java/com/practice
│   ├── MsCommonProductApplicationTests.java
│   ├── application/service/
│   │   └── ProductServiceTest.java
│   └── infrastructure/web/controller/
│       └── ProductRestControllerIntegrationTest.java
└── resources/
    └── application.yaml    ← H2 en memoria (no usa PostgreSQL local)
```

### Dependencias de prueba (`pom.xml`)

- `spring-boot-starter-test` — JUnit 5, Mockito, AssertJ, Spring Test.
- `h2` (scope `test`) — base en memoria solo para integración.

No hace falta declarar `junit-jupiter` ni `mockito-*` por separado: ya vienen en el starter de Spring Boot.

### Configuración H2 (`src/test/resources/application.yaml`)

En tests se sustituye PostgreSQL por H2:

- URL: `jdbc:h2:mem:ms_common_product_test`
- `ddl-auto: create-drop` — crea tablas al iniciar el test y las borra al terminar.

Así las pruebas de integración no dependen de tener PostgreSQL levantado en la máquina.

### Pruebas unitarias — `ProductServiceTest`

Archivo: `src/test/java/com/practice/application/service/ProductServiceTest.java`

| Método | Operación CRUD | Escenario |
|--------|----------------|-----------|
| `givenValidProduct_whenCreateProduct...` | Create | Alta válida → `ApplicationResult` ok y llamada a `save` |
| `givenInvalidPrice_whenCreateProduct...` | Create | Precio 0 → error `PRODUCT_PRICE_INVALID`, sin `save` |
| `givenExistingId_whenGetProductById...` | Read | Id existente → producto devuelto |
| `givenMissingId_whenGetProductById...` | Read | Id inexistente → `PRODUCT_NOT_FOUND` |
| `givenProductsInPort_whenFindAllProducts...` | Read | Listado con un elemento |
| `givenExistingProduct_whenUpdateProduct...` | Update | Actualización ok y `save` del dominio modificado |
| `givenExistingProduct_whenDeleteProduct...` | Delete | Baja ok y `deleteById` |

Ejemplo de lectura BDD en una unitaria:

1. **Given:** se prepara el producto y se simula `productPersistencePort.save(...)`.
2. **When:** se llama a `productService.createProduct(product)`.
3. **Then:** se asserta `isOk()`, el id y que Mockito verificó la interacción con el puerto.

### Pruebas de integración — `ProductRestControllerIntegrationTest`

Archivo: `src/test/java/com/practice/infrastructure/web/controller/ProductRestControllerIntegrationTest.java`

Usa `@SpringBootTest` + `@AutoConfigureMockMvc` + H2.

| Método | Endpoint | HTTP esperado |
|--------|----------|---------------|
| `givenValidRequest_whenCreateProduct...` | `POST /api/product/create-product` | 201 |
| `givenExistingProduct_whenFindProductById...` | `GET /api/product/find-product-by-id/{id}` | 200 |
| `givenExistingProduct_whenFindAllProducts...` | `GET /api/product/find-all-products` | 200 |
| `givenExistingProduct_whenUpdateProduct...` | `PUT /api/product/update-product/{id}` | 200 |
| `givenExistingProduct_whenDeleteProduct...` | `DELETE /api/product/delete-product/{id}` | 204 |

Flujo que recorre la prueba de integración (por ejemplo, crear):

```
MockMvc (petición HTTP simulada)
  → ProductRestController
  → ProductRestMapper (DTO → Product)
  → ProductService
  → JpaProductPersistenceAdapter
  → ProductPersistenceMapper
  → ProductJpaRepository
  → H2
```

Para GET, PUT y DELETE se usa el helper `persistProduct()`, que inserta datos en H2 antes del When, sin depender de que otro test haya corrido antes.

`@BeforeEach cleanDatabase()` ejecuta `deleteAll()` para que cada test arranque con la tabla vacía.

### Ejecutar las pruebas

Desde la carpeta `microservices-builder`:

```bash
mvn test -pl ms-common-product
```

Solo unitarias:

```bash
mvn test -pl ms-common-product -Dtest=ProductServiceTest
```

Solo integración:

```bash
mvn test -pl ms-common-product -Dtest=ProductRestControllerIntegrationTest
```

En IntelliJ: clic derecho en `src/test/java` → **Run 'All Tests'**.

Resultado esperado al ejecutar todo: **Tests run: 13** — coincide con el catálogo #1–#13 de arriba.

Para ejecutar solo un bloque:

| Quieres probar… | Comando |
|-----------------|---------|
| Solo la #1 (smoke) | `mvn test -pl ms-common-product -Dtest=MsCommonProductApplicationTests` |
| Solo #2–#8 (unitarias) | `mvn test -pl ms-common-product -Dtest=ProductServiceTest` |
| Solo #9–#13 (integración) | `mvn test -pl ms-common-product -Dtest=ProductRestControllerIntegrationTest` |

## Ejecutar la aplicación

```bash
mvn clean install -pl ms-common-product
mvn spring-boot:run -pl ms-common-product
```

Swagger: http://localhost:8081/swagger-ui.html
