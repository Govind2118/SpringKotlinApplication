# SpringKotlinApplication

A Kotlin + Spring WebFlux service that fetches cryptocurrency quotes from CoinGecko and exposes them through clean REST APIs.

## Features
- Single-quote endpoint for a coin/currency pair
- Batch quote endpoint for multiple target currencies
- In-memory TTL cache to reduce repeated external API calls
- Variable management endpoints for storing runtime values
- Centralized exception handling with consistent JSON error responses
- Health endpoint for quick service checks
- Coroutine-based non-blocking HTTP client using WebClient

## Endpoints
- `GET /api/v1/quotes/{coinId}/{targetCurrency}`
- `GET /api/v1/quotes/{coinId}?currencies=usd&currencies=inr`
- `POST /api/v1/variables`
- `GET /api/v1/variables/{name}`
- `GET /api/v1/health`

## Example requests
```bash
curl http://localhost:8000/api/v1/quotes/bitcoin/usd
curl "http://localhost:8000/api/v1/quotes/bitcoin?currencies=usd&currencies=inr"
curl -X POST http://localhost:8000/api/v1/variables \
  -H "Content-Type: application/json" \
  -d '{"name":"threshold","value":1.5}'
```

## Possible next improvements
- Persist variables to a database instead of memory
- Add request validation and Swagger/OpenAPI docs
- Add integration tests with MockWebServer
- Add Docker support and GitHub Actions CI
