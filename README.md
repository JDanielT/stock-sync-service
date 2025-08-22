# Stock Sync Service

Spring Boot app that aggregates inventory from multiple vendors, **persists per vendor** in the `product` table, and publishes a **`SyncCompletedEvent`** with totals **by SKU**. A listener (`ZeroStockService`) logs a warning when a SKU’s **aggregated** stock is zero.

## Build and run instructions

**Local (Maven)**
```bash
mvn clean verify
mvn spring-boot:run
```

**Docker**
```bash
docker build -t stock-sync-service .
docker run --rm -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -v /tmp/vendor-b:/tmp/vendor-b \
  stock-sync-service
```

**Swagger / OpenAPI**
- UI: `http://localhost:8080/swagger-ui.html`
- JSON: `http://localhost:8080/v3/api-docs`

## How to simulate Vendor A and Vendor B

- **Vendor A (REST-style mock)**  
  Returns a mocked list with **random** `stockQuantity` on each call. No external setup required.

- **Vendor B (CSV)**  
  Reads `/tmp/vendor-b/stock.csv`. On each sync, the app **rewrites** the CSV with **random** quantities and then reads it back.  
  To **force zero stock** for a SKU, edit the CSV and set `stockQuantity` to `0` for all entries of that SKU.

## Assumptions and decisions made

- Store **one row per `(sku, vendor)`** to preserve vendor-level state.
- Aggregate **after persistence** and publish a `SyncCompletedEvent` with per-SKU totals.
- Log “zero stock” **only when the aggregated total is 0** across vendors (e.g., A=5 & B=0 → no log).
- Use **H2** for dev/tests; expose a paginated `/products` endpoint (Spring `page/size/sort`).

## Trade-offs and ideas for improvement

- **Random data** is handy for simulation but non-deterministic for tests → add a flag to switch to fixed values.
- **In-memory events** are simple but transient → consider outbox + Kafka/RabbitMQ if consumers depend on them.
- **Notification noise** if a SKU stays at zero → add throttling (e.g., once/day per SKU) or track last-notified time.
- **Observability** → add Micrometer metrics (fetch latency, zero-stock count) and health/ready endpoints.
