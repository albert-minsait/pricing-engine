# Pricing Engine

Pricing Engine is a Spring Boot service developed as part of a technical assessment.

The project currently provides the initial application bootstrap and build configuration.

## Requirements

- Java 21
- Maven 3.9+

## Technologies

- Spring Boot
- Spring Web
- Spring Boot Test
- Maven
- Spotless
- Eclipse JDT Formatter

## Getting Started

### Build

Build the project:

```bash
mvn clean package
```

### Verify

Run the complete verification lifecycle, including code formatting verification:

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
├── formatter
│   └── eclipse-formatter.xml
└── src
    ├── main
    │   ├── java
    │   └── resources
    └── test
        ├── java
        └── resources
```

## License

This project is licensed under the MIT License.
