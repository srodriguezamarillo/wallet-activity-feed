# Wallet Activity Feed

Unified activity feed service consolidating financial transactions from multiple products (Card, P2P, Crypto, Earnings) into a single, searchable feed.

## Quick Start

```bash
# Run with sample data
./mvnw spring-boot:run

# Test the API
curl "http://localhost:8080/api/v1/activity?userId=user-123"
```

## Features

- **Unified feed** per user across all products
- **Filtering**: product, status, currency, date range
- **Search**: free-text search on metadata fields (merchantName, peerName, source)
- **Pagination**: page/size-based pagination using Spring Data
- **Real-time ingestion**: Kafka event consumption
- **Caching**: optional Redis cache for first page

## API

### List Activities

```bash
GET /api/v1/activity?userId={userId}[&product={product}][&status={status}][&currency={currency}][&from={iso8601}][&to={iso8601}][&search={text}][&page={n}][&size={n}]
```

**Examples:**

```bash
# All activities
curl "http://localhost:8080/api/v1/activity?userId=user-123"

# Filter by currency
curl "http://localhost:8080/api/v1/activity?userId=user-123&currency=USD"

# Search by merchant name
curl "http://localhost:8080/api/v1/activity?userId=user-123&search=burgertime"

# Combined filters
curl "http://localhost:8080/api/v1/activity?userId=user-123&product=CARD&status=COMPLETED&currency=USD&page=0&size=20"
```

**Response:**

```json
{
  "items": [
    {
      "id": "event-id",
      "product": "CARD",
      "type": "CARD_PAYMENT",
      "status": "COMPLETED",
      "title": "PEDIDOSYA*BURGERTIME",
      "subtitle": "CARD · 05 Dec 2025, 18:09",
      "incoming": false,
      "primaryAmount": {
        "currency": "UYU",
        "value": -2306.31
      },
      "occurredAt": "2025-12-05T18:09:58.250041Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1,
  "hasNext": false
}
```

### Get Activity Detail

```bash
GET /api/v1/activity/{id}
```

```bash
curl "http://localhost:8080/api/v1/activity/97986d3d-2933-4664-afd8-7637ae1de726"
```

## Setup

### Prerequisites

- JDK 17+
- Maven 3.8+
- Docker (optional, for Kafka/Redis)

### Database

The service uses MySQL. Configure in `application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/walletdb
    username: wallet
    password: wallet
```

Sample data is loaded on startup via `WalletActivityFeedApplication`.

### Kafka (Event Ingestion)

```bash
# Start Kafka
docker compose -f docker-compose.kafka.yaml up -d

# Run with Kafka enabled
./mvnw spring-boot:run -Dspring-boot.run.arguments="--app.kafka.enabled=true"

# Publish test event
curl -X POST http://localhost:8080/api/v1/activity/events \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user-123",
    "product": "CARD",
    "type": "CARD_PAYMENT",
    "amount": 10.50,
    "currency": "USD",
    "status": "COMPLETED",
    "occurredAt": "2025-01-01T12:00:00Z",
    "externalId": "tx-card-001",
    "metadata": {"merchantName": "DEMO STORE"}
  }'
```

### Redis (Caching)

```bash
# Start Redis
docker run --name wallet-redis -p 6379:6379 -d redis:7

# Run with Redis cache
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.cache.type=redis"
```

## Testing

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=ActivityFeedApiTests
```

## Error Handling & Logging

- Global exception handler returning structured JSON errors (400, 404, 500).
- Custom `ActivityNotFoundException` for missing resources instead of generic errors.
- Consistent `ErrorResponse` shape (`error`, `message`, `timestamp`, `path`, optional `details`).
- Kafka listener logs full context and rethrows on failure to let Kafka handle retries.
- Logback configured for readable console logs in dev and JSON logs in production (ready for log aggregation).

## Architecture Notes

- **Storage**: MySQL for transactional data, JSON column for flexible metadata
- **Search**: MySQL JSON functions for metadata search (JSON_EXTRACT on merchantName, peerName, source)
- **Scalability**: Current search implementation works for moderate volumes. For high-scale production (millions+ records), consider OpenSearch/Elasticsearch integration.
- **Event-driven**: Kafka-based ingestion from product microservices
- **Caching**: Redis cache for first page (page=0) to reduce DB load

## Requirements

- Java 17+
- Maven 3.8+
- MySQL 8.0+ (with JSON support)
- Docker (optional, for Kafka/Redis)
