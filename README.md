# Pricing Engine

Pricing Engine is a Spring Boot service developed as part of a technical assessment.

The project provides a Spring Boot service implementation following Clean
Architecture principles and domain-driven design practices.

## Requirements

- Java 21
- Maven 3.9+

## Technologies

| Category | Technologies |
| --- | --- |
| Build | Maven |
| Code Formatting | Spotless, Eclipse JDT Formatter |
| Code Quality | Checkstyle |
| Framework | Spring Boot |
| Web | Spring Web |
| Development Tools | Lombok |
| Testing | Spring Boot Test |

## Getting Started

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

### Format

Apply source formatting:

```bash
mvn spotless:apply
```

### Run

Start the application:

```bash
mvn spring-boot:run
```

The service is available at:

```text
http://localhost:8080
```

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
│   │   └── resources
│   └── test
│       └── java
│           └── es.inditex.pricingengine
├── .gitattributes
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

## Layers

The application follows a Clean Architecture approach, separating business rules
from application logic and infrastructure concerns.

### Domain Layer

The domain layer contains the core business concepts and rules of the Pricing
Engine.

This layer is independent of frameworks, persistence mechanisms and external
technologies.

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

#### Domain Model Decisions

- Domain entities are implemented as immutable classes.
- Domain entities are implemented as classes instead of records because their
  identity is defined by their identifiers rather than by all their attributes.
- Value objects are implemented using Java records because they represent
  immutable values identified by their content.
- Domain entities use Lombok only to reduce boilerplate code (`@Getter` and
  `@RequiredArgsConstructor`).
- Domain entities do not contain framework annotations such as JPA or Spring
  annotations.
- Monetary values are encapsulated in the `Money` value object, grouping the
  amount and currency to avoid handling them separately across the application.

### Application Layer

The application layer contains the application use cases and coordinates the
interaction between external actors and the domain layer.

This layer defines the operations that the application exposes and the external
dependencies required to execute them through input and output ports.

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

- The application layer follows a hexagonal architecture approach, separating
  inbound interactions from outbound dependencies through input and output
  ports.
- Input ports define the use cases exposed by the application. External
  adapters, such as REST controllers, depend on these abstractions instead of
  depending on concrete implementations.
- Output ports define the dependencies required by the application layer.
  These ports are implemented by infrastructure adapters and can be reused by
  different use cases.
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
  domain rules, persistence logic or framework-specific concerns.

## License

This project is licensed under the MIT License.
