# Pricing Engine

Pricing Engine is a Spring Boot service developed as part of a technical assessment.

The service exposes a REST API that determines the applicable selling price for a product based on the application date,
brand identifier and product identifier.

## Table of Contents

- [Requirements](#requirements)
- [Getting Started](#getting-started)
  - [Format](#format)
  - [Build](#build)
  - [Verify](#verify)
  - [Run](#run)
- [API Contract](#api-contract)
- [Technologies](#technologies)
- [Project Structure](#project-structure)
- [Documentation](#documentation)
- [License](#license)

## Requirements

- Java 21
- Maven 3.9+

## Getting Started

### Format

Apply source formatting:

```bash
mvn spotless:apply
```

### Build

Build the project:

```bash
mvn clean package
```

### Verify

Run the complete verification lifecycle:

```bash
mvn clean verify
```

### Run

Start the application:

```bash
mvn spring-boot:run
```

The application exposes the following endpoints:

| Resource | URL |
| --- | --- |
| REST API | http://localhost:8080/api/prices |
| OpenAPI Specification | http://localhost:8080/openapi/pricing-api.yaml |
| Swagger UI | http://localhost:8080/swagger-ui |
| H2 Console | http://localhost:8080/h2-console |

Default H2 connection settings:

| Property | Value |
| --- | --- |
| JDBC URL | `jdbc:h2:mem:pricing` |

## API Contract

The REST API contract is defined using an OpenAPI specification, which serves as the single source of truth for the HTTP
interface.

The OpenAPI specification is located at:

```text
src
└── main
    └── resources
        └── static
            └── openapi
                └── pricing-api.yaml
```

Error responses follow RFC 9457 (Problem Details for HTTP APIs) and are returned with
`Content-Type: application/problem+json`.

Further details about the API-first approach and the REST adapter implementation are available in
`docs/ARCHITECTURE.md`.

## Technologies

| Category | Technologies |
| --- | --- |
| Build | Maven |
| Framework | Spring Boot |
| Web | Spring Web, Bean Validation |
| API | OpenAPI 3.0, OpenAPI Generator, Swagger UI |
| Persistence | Jakarta Persistence, Spring Data JPA, H2, Flyway |
| Testing | Spring Boot Test, JUnit 5, AssertJ, Mockito |
| Development Tools | Lombok |
| Code Formatting | Spotless (Eclipse JDT Formatter) |
| Code Quality | Checkstyle |

## Project Structure

```text
.
├── .vscode
│   └── settings.json
├── config
│   ├── checkstyle.xml
│   ├── eclipse.importorder
│   └── formatter.xml
├── docs
│   └── ARCHITECTURE.md
├── src
│   ├── main
│   │   ├── java
│   │   │   └── es.inditex.pricingengine
│   │   │       └── ...
│   │   └── resources
│   │       ├── db
│   │       │   └── migration
│   │       │       ├── V1__create_prices_table.sql
│   │       │       └── V2__insert_prices_data.sql
│   │       ├── static
│   │       │   └── openapi
│   │       │       └── pricing-api.yaml
│   │       └── application.yaml
│   └── test
│       └── java
│           └── es.inditex.pricingengine
│               └── ...
├── .editorconfig
├── .gitattributes
├── .gitignore
├── README.md
├── mvnw
├── mvnw.cmd
└── pom.xml
```

## Documentation

Additional project documentation is available under the `docs` directory.

| Document | Description |
| --- | --- |
| `ARCHITECTURE.md` | Architectural style, domain boundaries, naming conventions, layer organization and design decisions. |

## License

This project is licensed under the MIT License.
