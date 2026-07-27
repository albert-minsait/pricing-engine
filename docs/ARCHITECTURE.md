# Architecture

This document describes the architectural principles, project organization and design decisions adopted by the Pricing
Engine.

It complements the information provided in the project `README.md` by documenting the internal structure of the
application.

## Table of Contents

- [Architecture Style](#architecture-style)
  - [Domain Boundaries](#domain-boundaries)
- [Naming Conventions](#naming-conventions)
  - [Package Naming](#package-naming)
  - [Type Naming](#type-naming)
  - [Singular and Plural Names](#singular-and-plural-names)
- [Layers](#layers)
  - [Domain Layer](#domain-layer)
    - [Domain Layer Decisions](#domain-layer-decisions)
  - [Application Layer](#application-layer)
    - [Application Layer Decisions](#application-layer-decisions)
  - [Infrastructure Layer](#infrastructure-layer)
    - [Inbound REST Adapter](#inbound-rest-adapter)
      - [Inbound REST Adapter Decisions](#inbound-rest-adapter-decisions)
    - [Outbound JPA Adapter](#outbound-jpa-adapter)
      - [Outbound JPA Adapter Decisions](#outbound-jpa-adapter-decisions)
- [Error Handling](#error-handling)
  - [Application Layer Exceptions](#application-layer-exceptions)
  - [Infrastructure Exception Handler](#infrastructure-exception-handler)
  - [RFC 9457 Problem Details](#rfc-9457-problem-details)

## Architecture Style

The application follows **Clean Architecture** principles together with **Domain-Driven Design (DDD)** practices.

The service is intentionally scoped to the **Pricing** bounded context, whose single responsibility is determining the
applicable selling price for a product based on the brand identifier, product identifier, and application date.

### Domain Boundaries

Although the original statement describes `BRAND_ID` as a foreign key, the Pricing Engine models only the Pricing
bounded context rather than the complete enterprise domain.

The Pricing bounded context owns only pricing information and therefore persists a single `prices` table. The
`brand_id` and `product_id` fields are treated as references to external domain concepts instead of entities owned by
the service.

In a real-world distributed architecture, Brand and Product would typically be implemented as independent services, each
one owning its own bounded context, business rules and persistence model. Those services could expose REST APIs for
synchronous queries and publish domain events whenever their master data changes.

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

This design allows Brand and Product to evolve independently while the Pricing Engine remains focused exclusively on
pricing rules and price determination.

The approach preserves clear ownership boundaries, avoids unnecessary duplication of master data across bounded contexts
and keeps the service focused on its business responsibility.

## Naming Conventions

The project follows consistent naming conventions to distinguish business concepts, architectural responsibilities and
technology-specific components.

### Package Naming

Packages are organized by architectural layer, adapter direction and technology.

| Architectural Layer | Package | Purpose |
| --- | --- | --- |
| Domain | `domain.model` | Domain entities. |
| Domain | `domain.vo` | Domain value objects. |
| Application | `application.port.input` | Input ports exposed by the application layer. |
| Application | `application.port.output` | Output ports required by the application layer. |
| Application | `application.usecase.<capability>` | Application use case implementations grouped by business capability. |
| Application | `application.exception` | Application layer exceptions. |
| Infrastructure | `infrastructure.inbound.rest.api` | OpenAPI-generated server interfaces and controller. |
| Infrastructure | `infrastructure.inbound.rest.adapter` | REST inbound adapter implementations. |
| Infrastructure | `infrastructure.inbound.rest.mapper` | REST model mappers. |
| Infrastructure | `infrastructure.inbound.rest.handler` | REST exception handler. |
| Infrastructure | `infrastructure.inbound.rest.model` | OpenAPI-generated API models. |
| Infrastructure | `infrastructure.outbound.jpa.repository` | Spring Data JPA repositories. |
| Infrastructure | `infrastructure.outbound.jpa.adapter` | JPA outbound adapter implementations. |
| Infrastructure | `infrastructure.outbound.jpa.mapper` | Persistence mappers. |
| Infrastructure | `infrastructure.outbound.jpa.entity` | JPA persistence entities. |

### Type Naming

| Architectural Layer | Naming Convention | Purpose | Example |
| --- | --- | --- | --- |
| Application | `UseCase` | Application input port. | `GetPriceUseCase` |
| Application | `Interactor` | Application use case implementation. | `GetPriceInteractor` |
| Application | `Result` | Use case-specific result model. | `GetPriceResult` |
| Application | `Repository` | Application output port. | `PriceRepository` |
| Infrastructure | `Api`, `ApiController`, `ApiDelegate` | OpenAPI-generated server types. | `PricesApi`, `PricesApiController`, `PricesApiDelegate` |
| Infrastructure | `Adapter` | Infrastructure adapter implementations. | `PriceRestAdapter`, `PriceRepositoryAdapter` |
| Infrastructure | `Mapper` | Converts models between architectural layers. | `PriceRestMapper`, `PriceEntityMapper` |
| Infrastructure | `Handler` | Handles REST exceptions and cross-cutting concerns. | `RestExceptionHandler` |
| Infrastructure | `Response` | OpenAPI-generated response model. | `PriceResponse` |
| Infrastructure | `Entity` | JPA persistence model. | `PriceEntity` |

**Note:** OpenAPI Generator also generates supporting API models, such as `ProblemDetail`, defined by the
OpenAPI specification. These types are omitted from the table because they are generated rather than
project-specific naming conventions.

> **Naming guideline**
>
> Class names primarily reflect their architectural role rather than the implementation technology.
>
> Outbound adapters are named after the application output port they implement, reflecting the responsibility they
> provide to the application. For example, `PriceRepositoryAdapter` implements the `PriceRepository` output port.
>
> Inbound adapters are named after the adapted technology, reflecting the integration point they expose. For example,
> `PriceRestAdapter` and `PriceRestMapper`.

### Singular and Plural Names

REST resources follow standard REST conventions and are therefore expressed using plural names.

```text
GET /prices
```

Business concepts remain singular throughout the codebase.

| Plural (REST resource) | Singular (business concept) |
| --- | --- |
| `/prices` | `Price` |
| `PricesApi` | `PriceRepository` |
| `PricesApiController` | `PriceRepositoryAdapter` |
| `PricesApiDelegate` | `PriceRestAdapter` |
| — | `PriceRestMapper` |

## Layers

The application is organized into three architectural layers following the principles of Clean Architecture.

### Domain Layer

The domain layer contains the core business concepts and business rules of the Pricing Engine.

This layer is completely independent of frameworks, persistence mechanisms and external technologies.

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
- Domain entities are implemented as classes instead of records because their identity is defined by their identifiers
  rather than by all their attributes.
- Value objects are implemented using Java records because they represent immutable values identified by their content.
- Domain entities use Lombok only to reduce boilerplate code through `@Getter` and `@RequiredArgsConstructor`.
- Domain entities do not contain framework-specific annotations such as JPA or Spring annotations.
- Monetary values are encapsulated in the `Money` value object, grouping the amount and currency to avoid handling them
  separately across the application.

### Application Layer

The application layer contains the application use cases and coordinates the interaction between external actors and the
domain layer.

This layer defines the operations exposed by the application together with the external dependencies required to execute
them through input and output ports.

Current application structure:

```text
application
├── exception
│   └── PriceNotFoundException.java
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

- The application layer follows a Hexagonal Architecture approach, separating inbound interactions from outbound
  dependencies through input and output ports.
- Input ports define the use cases exposed by the application. External adapters, such as REST controllers, depend on
  these abstractions instead of concrete implementations.
- Output ports define the dependencies required by the application layer. Infrastructure adapters implement these ports
  and can be reused by different use cases.
- Use case implementations are organized by business capability rather than by technical type. This keeps related
  application logic together and allows the application layer to scale as new use cases are added.
- Use case implementations are named as interactors because they represent the execution of a specific application use
  case, avoiding generic names such as service or implementation classes.
- Use case results are scoped to their corresponding use case instead of being placed in shared application models. This
  avoids creating generic containers shared by unrelated operations.
- The application layer orchestrates use cases by coordinating domain operations. It does not contain domain business
  rules, persistence logic or framework-specific concerns.
- The application layer raises domain-specific exceptions for business-level conditions that occur during use case
  execution (not for domain rule violations). These exceptions encapsulate business context enabling upstream adapters
  to provide meaningful, context-aware error responses. Example: `PriceNotFoundException` represents the business
  condition "no applicable price found for the given criteria", carrying the search parameters (brand, product,
  application date) for error reporting.

### Infrastructure Layer

The infrastructure layer contains the technical implementations required by the application and provides the adapters
that connect the application to frameworks, databases, external systems, and other infrastructure concerns.

This layer groups its components into inbound and outbound adapters, keeping the domain and application layers isolated
from framework-specific concerns while allowing each integration to evolve independently.

#### Inbound REST Adapter

The inbound REST adapter exposes the pricing use case through a REST API.

The service follows an API-first approach. The OpenAPI specification is the single source of truth for the HTTP
contract.

During the build, OpenAPI Generator produces the server API interface, controller, delegate interface and API models.
The inbound adapter implements the generated delegate interface, maps API requests to the application layer and
transforms application responses into the generated API models.

Current inbound REST adapter structure:

```text
infrastructure
└── inbound
    └── rest
        ├── adapter
        │   └── PriceRestAdapter.java
        ├── api
        │   ├── ApiUtil.java (generated)
        │   ├── PricesApi.java (generated)
        │   ├── PricesApiController.java (generated)
        │   └── PricesApiDelegate.java (generated)
        ├── handler
        │   └── RestExceptionHandler.java
        ├── mapper
        │   └── PriceRestMapper.java
        └── model
            ├── PriceResponse.java (generated)
            └── ProblemDetail.java (generated)
```

##### Inbound REST Adapter Decisions

- The REST API follows an API-first approach.
- The OpenAPI specification is the single source of truth for the API contract.
- OpenAPI Generator generates the server API interface, controller, delegate interface and API models during the
  build.
- The inbound REST adapter contains no business logic and delegates use case execution to the application layer.
- The delegate pattern is used to keep the REST API infrastructure generated while allowing the inbound adapter
  implementation to remain independent of the generated code.
- Dedicated mappers translate between generated API models and application models.
- Date and time values are represented using `LocalDateTime` because the provided data model stores timestamps without
  time zone information. Time zone handling is outside the scope of this exercise.
- Error responses follow RFC 9457 (Problem Details for HTTP APIs) standard, providing consistent and standardized error
  handling across the API.

#### Outbound JPA Adapter

The outbound JPA adapter implements the application output port and provides database integration using Spring Data JPA.

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

- The outbound JPA adapter implements the PriceRepository output port defined by the application layer.
- Spring Data repositories remain internal to the outbound adapter and are never exposed to the application layer.
- JPA entities are isolated from the domain model. Dedicated mappers translate between persistence entities and domain
  objects.
- The project uses an H2 in-memory database for local development and testing.
- Database schema and reference data are managed through Flyway versioned SQL migrations.
- Flyway automatically executes migrations during application startup, ensuring that the database schema and reference
  data remain consistent.
- Migration scripts are located under `src/main/resources/db/migration`, following Flyway's default conventions.

## Error Handling

Error handling is a transversal concern spanning the application and infrastructure layers.

The application layer raises business exceptions carrying rich context. The infrastructure layer intercepts them and
maps them to RFC 9457 Problem Detail responses.

### Application Layer Exceptions

Application exceptions represent business-level conditions that occur during use case execution. They are defined in
the `application.exception` package and carry business context to enable meaningful error responses.

| Exception | Condition | Business Context |
| --- | --- | --- |
| `PriceNotFoundException` | No applicable price found | Brand ID, Product ID, Application Date |

### Infrastructure Exception Handler

`RestExceptionHandler` in `infrastructure.inbound.rest.handler` is annotated with `@RestControllerAdvice` and maps
application and framework exceptions to RFC 9457 Problem Detail responses.

| Exception | HTTP Status | Problem Type |
| --- | --- | --- |
| `MissingServletRequestParameterException` | 400 | `urn:problem-type:pricing:invalid-request` |
| `MethodArgumentTypeMismatchException` | 400 | `urn:problem-type:pricing:invalid-request` |
| `ConstraintViolationException` | 400 | `urn:problem-type:pricing:invalid-request` |
| `HandlerMethodValidationException` | 400 | `urn:problem-type:pricing:invalid-request` |
| `PriceNotFoundException` | 404 | `urn:problem-type:pricing:price-not-found` |
| `Exception` | 500 | `urn:problem-type:pricing:unexpected-error` |

### RFC 9457 Problem Details

All error responses follow RFC 9457 (Problem Details for HTTP APIs) and are returned with
`Content-Type: application/problem+json`.

Every error response includes the following fields:

| Field | Type | Description |
| --- | --- | --- |
| `type` | URI | URN identifying the problem type. |
| `title` | String | Short human-readable summary of the problem. |
| `status` | Integer | HTTP status code. |
| `detail` | String | Human-readable explanation specific to this occurrence of the problem. |
| `instance` | URI | URI identifying the specific request that caused the problem. |
