# ProCalc

A simple programmable calculator REST API built with Spring Boot.

## Build & Run

- Prerequisites: Java 17+, Maven 3.9+

```bash
mvn spring-boot:run
```

Or build a jar and run:

```bash
mvn clean package
java -jar target/procalc-0.0.1-SNAPSHOT.jar
```

## API

POST /api/v1/calc/evaluate

Request body:
```json
{ "expression": "(2+3)*4/5" }
```

Response body:
```json
{
  "expression": "(2+3)*4/5",
  "result": 4.0,
  "rpn": ["2","3","+","4","*","5","/"]
}
```

## Notes

- Supports +, -, *, / and parentheses.
- Includes basic error handling and an in-memory history/memory service.
# ProCalc (Java Spring Boot)

## Run locally
1. Build:
   mvn -DskipTests package

2. Run:
   java -Dserver.port=8080 -jar target/procalc-0.0.1.jar

3. API:
   POST /api/v1/evaluate  { "expression": "2 + 3 * 4" }
   POST /api/v1/preview   { "expression": "2 + 3 *" }
   GET  /api/v1/history
   GET  /api/v1/memory
