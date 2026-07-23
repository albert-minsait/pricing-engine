# Pricing Engine

Pricing Engine is a Spring Boot service developed as part of a technical assessment.

The project currently provides the initial application bootstrap and build configuration.

## Requirements

- Java 21
- Maven 3.9+

## Technologies

| Category | Technologies |
| --- | --- |
| Build | Maven |
| Code Formatting | Spotless, Eclipse JDT Formatter |
| Framework | Spring Boot |
| Web | Spring Web |
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

## License

This project is licensed under the MIT License.
