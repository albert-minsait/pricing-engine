# Pricing Engine

Pricing Engine is a Spring Boot service developed as part of a technical
assessment.

The service exposes a REST API that determines the applicable selling price
for a product based on the application date, brand identifier and product
identifier.

## Table of Contents

- [Requirements](#requirements)
- [Getting Started](#getting-started)
  - [Format](#format)
  - [Build](#build)
  - [Verify](#verify)
  - [Run](#run)
- [Architecture](#architecture)
  - [Domain Boundaries](#domain-boundaries)
- [Technologies](#technologies)
- [Project Structure](#project-structure)
- [Layers](#layers)
  - [Domain Layer](#domain-layer)
    - [Domain Layer Decisions](#domain-model-decisions)
  - [Application Layer](#application-layer)
    - [Application Layer Decisions](#application-layer-decisions)
- [Infrastructure Layer](#infrastructure-layer)
  - [Outbound JPA Adapter](#outbound-jpa-adapter)
    - [Outbound JPA Adapter Decisions](#outbound-jpa-adapter-decisions)
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

The application is available at:

| Service | URL |
| --- | --- |
| REST API | http://localhost:8080 |
| H2 Console | http://localhost:8080/h2-console |

Default H2 connection settings:

| Property | Value |
| --- | --- |
| JDBC URL | `jdbc:h2:mem:pricing` |

## Architecture

The application follows **Clean Architecture** principles together with
**Domain-Driven Design (DDD)** practices.

The service is intentionally scoped to the **Pricing** bounded context, whose
single responsibility is determining the applicable selling price for a
product based on the brand identifier, product identifier, and application
date.

### Domain Boundaries

Although the original statement describes `BRAND_ID` as a foreign key, the
Pricing Engine models only the Pricing bounded context rather than the
complete enterprise domain.

The Pricing bounded context owns only pricing information and therefore
persists a single `prices` table. The `brand_id` and `product_id` fields are
treated as references to external domain concepts instead of entities owned by
the service.

In a real-world distributed architecture, Brand and Product would typically be
implemented as independent services, each one owning its own bounded context,
business rules and persistence model. Those services could expose REST APIs for
synchronous queries and publish domain events whenever their master data
changes.

```text
                +-----------------+
                |  Brand Service  |
                +-----------------+
                        │
          REST API / Domain Events
                        │
                        ▼
                +-----------------+
                | Pricing Engine  |
                +-----------------+
                        ▲
                        │
          REST API / Domain Events
                        │
                +-----------------+
                | Product Service |
                +-----------------+
```

This design allows Brand and Product to evolve independently while the Pricing
Engine remains focused exclusively on pricing rules and price determination.

The approach preserves clear ownership boundaries, avoids unnecessary
duplication of master data across bounded contexts and keeps the service
focused on its business responsibility.

## Technologies

| Category | Technologies |
| --- | --- |
| Build | Maven |
| Framework | Spring Boot |
| Web | Spring Web |
| Persistence | Spring Data JPA, H2, Flyway |
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
│   └── formatter.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   └── es.inditex.pricingengine
│   │   │       └── ...
│   │   └── resources
│   │       └── application.yml
│   └── test
│       └── java
│           └── es.inditex.pricingengine
│               └── ...
├── .editorconfig
├── .gitattributes
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

## Layers

The application follows a Clean Architecture approach, separating business
rules from application logic and infrastructure concerns.

### Domain Layer

The domain layer contains the core business concepts and business rules of the
Pricing Engine.

This layer is completely independent of frameworks, persistence mechanisms and
external technologies.

Current domain model:

```text
domain
├── model
│   ├── Brand.java
│   ├── Product.java
│   └── Price.java
├── vo
│   ├── BrandId.java
│   ├── ProductId.java
│   └── Money.java
└── package-info.java
```

#### Domain Layer Decisions

- Domain entities are implemented as immutable classes.
- Domain entities are implemented as classes instead of records because their
  identity is defined by their identifiers rather than by all their
  attributes.
- Value objects are implemented using Java records because they represent
  immutable values identified by their content.
- Domain entities use Lombok only to reduce boilerplate code through
  `@Getter` and `@RequiredArgsConstructor`.
- Domain entities do not contain framework-specific annotations such as JPA or
  Spring annotations.
- Monetary values are encapsulated in the `Money` value object, grouping the
  amount and currency to avoid handling them separately across the
  application.

### Application Layer

The application layer contains the application use cases and coordinates the
interaction between external actors and the domain layer.

This layer defines the operations exposed by the application together with the
external dependencies required to execute them through input and output ports.

Current application structure:

```text
application
├── port
│   ├── input
│   │   └── GetPriceUseCase.java
│   └── output
│       └── PriceRepository.java
├── usecase
│   └── getprice
│       ├── GetPriceInteractor.java
│       └── result
│           └── GetPriceResult.java
└── package-info.java
```

#### Application Layer Decisions

- The application layer follows a Hexagonal Architecture approach, separating
  inbound interactions from outbound dependencies through input and output
  ports.
- Input ports define the use cases exposed by the application. External
  adapters, such as REST controllers, depend on these abstractions instead of
  concrete implementations.
- Output ports define the dependencies required by the application layer.
  Infrastructure adapters implement these ports and can be reused by different
  use cases.
- Use case implementations are organized by business capability rather than by
  technical type. This keeps related application logic together and allows the
  application layer to scale as new use cases are added.
- Use case implementations are named as interactors because they represent the
  execution of a specific application use case, avoiding generic names such as
  service or implementation classes.
- Use case results are scoped to their corresponding use case instead of being
  placed in shared application models. This avoids creating generic containers
  shared by unrelated operations.
- The application layer coordinates domain operations but does not contain
  business rules, persistence logic or framework-specific concerns.

### Infrastructure Layer

The infrastructure layer contains the technical implementations required by the
application and provides the adapters that connect the application to
frameworks, databases, external systems, and other infrastructure concerns.

This layer groups its components into inbound and outbound adapters,
keeping the domain and application layers isolated from framework-specific
concerns while allowing each integration to evolve independently.

#### Outbound JPA Adapter

The outbound JPA adapter implements the application output port and provides
database integration using Spring Data JPA.

Current outbound JPA adapter structure:

```text
infrastructure
└── outbound
    └── jpa
        ├── adapter
        │   └── PriceRepositoryAdapter.java
        ├── entity
        │   └── PriceEntity.java
        ├── mapper
        │   └── PriceEntityMapper.java
        └── repository
            └── PriceJpaRepository.java
```

Database migrations:

```text
src/main/resources
└── db
    └── migration
        ├── V1__create_prices_table.sql
        └── V2__insert_prices_data.sql
```

##### Outbound JPA Adapter Decisions

- The outbound JPA adapter implements the PriceRepository output port defined
  by the application layer.
- Spring Data repositories remain internal to the outbound adapter and are
  never exposed to the application layer.
- JPA entities are isolated from the domain model. Dedicated mappers translate
  between persistence entities and domain objects.
- The project uses an H2 in-memory database for local development and testing.
- Database schema and reference data are managed through Flyway versioned SQL
  migrations.
- Flyway automatically executes migrations during application startup,
  ensuring that the database schema and reference data remain consistent.
- Migration scripts are located under
  `src/main/resources/db/migration`, following Flyway's default conventions.

## License

This project is licensed under the MIT License.
