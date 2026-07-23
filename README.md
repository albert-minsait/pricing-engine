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

## License

This project is licensed under the MIT License.
