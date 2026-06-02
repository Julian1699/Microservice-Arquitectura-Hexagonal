# Arquitectura Hexagonal — Monorepo de práctica

Repositorio de estudio con microservicios en **Java 17**, **Spring Boot 3** y **Maven**, organizados bajo arquitectura hexagonal (puertos y adaptadores).

## Estructura

```
Arquitectura-Hexagonal/
├── .gitignore                 # Reglas globales (IDE, target, generados)
├── README.md                  # Este archivo
└── microservices-builder/     # Parent Maven (multi-módulo)
    ├── pom.xml
    └── ms-common-product/       # Microservicio de productos (CRUD)
        ├── pom.xml
        ├── README.md            # Documentación detallada del micro
        └── postman/             # Colección Postman
```

## Módulos

| Módulo | Descripción | Puerto |
|--------|-------------|--------|
| `ms-common-product` | CRUD de productos, hexagonal + Swagger UI | `8081` |

## Requisitos

- JDK 17+
- Maven 3.9+
- PostgreSQL (solo para ejecución local del microservicio)

## Comandos útiles

```bash
# Compilar todo el monorepo
cd microservices-builder
mvn clean install

# Solo el micro de productos
mvn clean test -pl ms-common-product

# Levantar la aplicación
mvn spring-boot:run -pl ms-common-product
```

## Tests

Los módulos usan **JUnit 5** y **Mockito** (declarados en cada `pom.xml` del microservicio).  
`spring-boot-starter-test` agrupa el stack de pruebas de Spring Boot.

```bash
mvn test -pl ms-common-product
```

## Qué no se versiona

El `.gitignore` de la raíz excluye, en cualquier nivel:

- Carpetas `.idea/`, `*.iml`, `.vscode/`
- `target/`, `build/`, código generado (`generated-sources`, MapStruct `*MapperImpl`)
- Secretos (`.env`), logs y artefactos locales

Si `.idea` quedó trackeada antes del ignore, quitarla del índice de Git:

```bash
git rm -r --cached .idea
git commit -m "chore: dejar de versionar configuración de IDE"
```

## Documentación por módulo

Detalle de arquitectura, endpoints y Swagger: ver [microservices-builder/ms-common-product/README.md](microservices-builder/ms-common-product/README.md).
