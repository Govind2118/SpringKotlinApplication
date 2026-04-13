# coin-conversion-api

A production-style Kotlin + Spring Boot WebFlux application for retrieving cryptocurrency quotes from CoinGecko, caching responses, persisting user-defined variables, and auditing quote history.

## Features
- Versioned REST APIs
- Validation and centralized exception handling
- In-memory TTL cache for external API responses
- Persistent storage using H2 + JDBC
- Quote audit history endpoint
- Actuator health/metrics endpoints
- Swagger/OpenAPI UI
- GitHub Actions CI
- Dockerfile for containerized deployment

## Tech stack
- Kotlin
- Spring Boot 2.7
- Spring WebFlux + coroutines
- JDBC + H2
- Spring Validation
- Spring Actuator
- springdoc OpenAPI
- MockK + JUnit 5

## Endpoints
### Quotes
- `GET /api/v1/quotes/{coinId}/{targetCurrency}`
- `GET /api/v1/quotes/{coinId}?currencies=usd&currencies=inr`
- `GET /api/v1/quotes/history/{coinId}`

### Variables
- `POST /api/v1/variables`
- `GET /api/v1/variables/{name}`

### Operational endpoints
- `GET /api/v1/health`
- `GET /actuator/health`
- `GET /actuator/metrics`
- `GET /swagger-ui.html`

## Example usage
```bash
curl http://localhost:8000/api/v1/quotes/bitcoin/usd
curl "http://localhost:8000/api/v1/quotes/bitcoin?currencies=usd&currencies=inr"
curl -X POST http://localhost:8000/api/v1/variables \
  -H "Content-Type: application/json" \
  -d '{"name":"threshold","value":1.5}'
curl http://localhost:8000/api/v1/variables/threshold
curl http://localhost:8000/api/v1/quotes/history/bitcoin
```

## Running locally
```bash
./gradlew bootRun
```

The app runs on `http://localhost:8000`.

## Build a jar
```bash
./gradlew clean test bootJar
```

## Run with Docker
```bash
./gradlew bootJar
docker build -t crypto-quote-service .
docker run -p 8000:8000 crypto-quote-service
```