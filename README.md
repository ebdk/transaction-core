# Transaction Core

Hexagonal Spring Boot service that fulfils the Mendel REST challenge using Java 21 features (records, `var`, stream APIs) and an in-memory persistence adapter. The service stores transactions via idempotent `PUT` requests, lists ids by type, and returns the recursive sum for any transaction chain.

## Requirements

- Java 21
- Gradle 8.14 (wrapper included)
- Docker (optional, for container image)

## Architecture

- **Domain** – `Transaction` record, `TransactionType`, and ports describing use cases (`PutTransactionInput`, `GetTransactionsIdsByTypeUseCase`, `GetTransactionSumUseCase`) as pure contracts.
- **Application** – Use-case implementations orchestrating domain behaviour and enforcing invariants. They only depend on domain ports.
- **Infrastructure adapters**
  - `adapter.out.persistence` – `InMemoryTransactionRepository` implementing `TransactionRepository`.
  - `adapter.in.web` – REST controller, DTOs, and exception handler exposing the HTTP API.

This keeps the core logic independent from delivery or storage technologies while still being testable (unit and integration suites cover every use case).

## API

| Method | Endpoint | Description |
| --- | --- | --- |
| `PUT` | `/transactions/{transactionId}` | Creates or replaces a transaction using the provided id. Body: `{ "amount": 1000.0, "type": "deposit", "parent_id": 10 }`. `type` must be `deposit` or `withdrawal`. Returns `{ "status": "ok" }`. |
| `GET` | `/transactions/types/{type}` | Returns `[id1, id2, ...]` sorted ascending for the given type (`deposit` or `withdrawal`). |
| `GET` | `/transactions/sum/{transactionId}` | Returns `{ "sum": 20000.0 }` for the transaction and every descendant linked through `parentId`. |

See `src/test/resources/contracts` for sample requests/responses that back the integration tests.

## Running locally

```bash
./gradlew bootRun
```

Tests (unit + integration):

```bash
./gradlew test
```

## Docker

Build the image:

```bash
docker build -t transaction-core .
```

Run it:

```bash
docker run -p 8080:8080 transaction-core
```

Environment variables can be added later if persistence or configuration modules evolve, but the current implementation runs fully in memory.
