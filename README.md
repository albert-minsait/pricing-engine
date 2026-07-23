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

## Getting Started

### Build

Build the project:

```bash
mvn clean package
```

### Test

Run all tests:

```bash
mvn clean verify
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
src
├── main
│   ├── java
│   └── resources
└── test
    ├── java
    └── resources
```

## License

This project is licensed under the MIT License.
