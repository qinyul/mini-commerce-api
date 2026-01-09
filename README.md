# Mini Commerce API

A Spring Boot mini project demonstrating **clean architecture**, **transactional consistency**, and **stock concurrency control**.

---

## 🚀 Features

- Product management (CRUD)
- Stock management with pessimistic locking
- Order lifecycle with stock reservation
- Transactional consistency
- Pagination & sorting validation
- Swagger / OpenAPI documentation

---

## 🏗️ Architecture Design

The application follows a strict layered architecture to separate concerns.

```text
┌───────────────────────────┐
│        Controller         │  ⬅️ REST Interface
│  (Product, Stock, Order)  │     (Validation & DTO Mapping)
└─────────────┬─────────────┘
              │
              ▼
┌─────────────┴─────────────┐
│          Service          │  ⬅️ Transaction Boundary
│   (Business Rules & Tx)   │     (@Transactional)
└─────────────┬─────────────┘
              │
              ▼
┌─────────────┴─────────────┐
│        Repository         │  ⬅️ Data Access Layer
│  (JPA / Hibernate / SQL)  │     (Interface only)
└───────────────────────────┘
```

## 💾 Database Schema

The database is normalized to 3NF, with foreign key constraints ensuring data integrity.

### `products`

_The central catalog definition._

| Column        | Type        | Constraints          | Description                                |
| :------------ | :---------- | :------------------- | :----------------------------------------- |
| `id`          | `BIGINT`    | **PK**, Auto Inc     | Internal unique identifier                 |
| `externalId`  | `UUID`      | **UNIQUE**, Indexed  | **Public Key:** Exposed in API URLs.       |
| `code`        | `VARCHAR`   | **UNIQUE**, NOT NULL | Business key / SKU (e.g., "SKU-001")       |
| `name`        | `VARCHAR`   | NOT NULL             | Display name                               |
| `active`      | `BOOLEAN`   | Default **TRUE**     | Soft-delete flag (never delete rows).      |
| `version`     | `BIGINT`    | NOT NULL             | Optimistic Locking: Prevents lost updates. |
| `description` | `TEXT`      | NULLABLE             | Product details (Markdown supported)       |
| `created_at`  | `TIMESTAMP` | NOT NULL             | Audit timestamp                            |
| `updated_at`  | `TIMESTAMP` | NULLABLE             | Audit timestamp                            |

### `stocks`

_Inventory storage. One-to-One relationship with Products._

| Column       | Type      | Constraints          | Description                               |
| :----------- | :-------- | :------------------- | :---------------------------------------- |
| `id`         | `BIGINT`  | **PK**               | Internal unique identifier                |
| `externalId` | `UUID`    | **UNIQUE**, Indexed  | **Public Key:** Exposed in API URLs.      |
| `product_id` | `UUID`    | **FK** (products.id) | The product this stock belongs to         |
| `quantity`   | `INTEGER` | CHECK (qty >= 0)     | **Critical:** The Available-to-Sell count |
| `version`    | `BIGINT`  | NULLABLE             | Used for Optimistic Locking (Future use)  |

### `orders`

_The transaction header._

| Column       | Type        | Constraints         | Description                                               |
| :----------- | :---------- | :------------------ | :-------------------------------------------------------- |
| `id`         | `BIGINT`    | **PK**              | Internal unique identifier                                |
| `externalId` | `UUID`      | **UNIQUE**, Indexed | **Public Key:** Exposed in API URLs.                      |
| `status`     | `VARCHAR`   | NOT NULL            | `PENDING`, `CONFIRMED`, `CANCELLED`,`CREATED`,`COMPLETED` |
| `version`    | `BIGINT`    | NOT NULL            | Optimistic Locking: Prevents lost updates.                |
| `created_at` | `TIMESTAMP` | NOT NULL            | When the checkout started                                 |

### `order_items`

_Line items for an order. Implements "Snapshotting" pattern._

| Column         | Type      | Constraints          | Description                                      |
| :------------- | :-------- | :------------------- | :----------------------------------------------- |
| `id`           | `BIGINT`  | **PK**               | Internal unique identifier                       |
| `order_id`     | `UUID`    | **FK** (orders.id)   | Parent order reference                           |
| `product_id`   | `UUID`    | **FK** (products.id) | Link to live product definition                  |
| `quantity`     | `INTEGER` | CHECK (qty > 0)      | Number of units purchased                        |
| `product_code` | `VARCHAR` | NOT NULL             | **Snapshot:** Preserves code at time of purchase |
| `product_name` | `VARCHAR` | NOT NULL             | **Snapshot:** Preserves name at time of purchase |

## 🔐 Transaction & Concurrency Design

### 🔐 Stock Reservation & Concurrency

To prevent race conditions (overselling), the system implements a strict **Pessimistic Locking** strategy during the checkout phase.

**The Transaction Flow (`OrderService.confirmOrder`)**

```text
[Transaction Start]
   │
   ├─ 1. Load Order Entity
   │
   ├─ 2. Iterate Order Items
   │     │
   │     ├─ A. LOCK: SELECT * FROM stocks WHERE product_id = ? FOR UPDATE
   │     │     (Other transactions waiting for this lock will block here)
   │     │
   │     ├─ B. CHECK: if (stock.quantity < request.quantity) throw Error
   │     │
   │     └─ C. UPDATE: stock.quantity = stock.quantity - request.quantity
   │
   ├─ 3. Update Order Status -> CONFIRMED
   │
   └─ 4. Commit Transaction (Locks Released)
```

- **Pessimistic Locking (`FOR UPDATE`)** ensures:

  - No overselling
  - Safe concurrent order confirmation

- **Transactional boundary** is enforced at the service layer.

---

## 🧠 Design Principles

- Controller = HTTP only (no business logic)
- Service = business rules + transactions
- Repository = data access only
- DTOs for API boundaries
- Domain-driven naming

---

## 📚 API Documentation

Swagger UI is enabled using **springdoc-openapi**.

| URL                | Description                   |
| ------------------ | ----------------------------- |
| `/swagger-ui.html` | Interactive API documentation |
| `/v3/api-docs`     | OpenAPI JSON                  |

---

## 🧪 Validation & Error Handling

The API implements a centralized error handling strategy using `@RestControllerAdvice`. It strictly separates **Client Errors (4xx)** from **Server Errors (5xx)** and leverages the **RFC 7807 Problem Details** standard for validation failures.

### Exception Mapping Strategy

| Exception                         | HTTP Status            | Description                                                                         |
| :-------------------------------- | :--------------------- | :---------------------------------------------------------------------------------- |
| `MethodArgumentNotValidException` | **400 Bad Request**    | Returns a detailed `ProblemDetail` object listing specific field validation errors. |
| `EntityAlreadyExistsException`    | **400 Bad Request**    | Triggered when violating business uniqueness rules (non-database).                  |
| `EntityNotFoundException`         | **404 Not Found**      | Triggered when accessing non-existent IDs.                                          |
| `DuplicateResourceException`      | **409 Conflict**       | Triggered on database constraint violations (e.g., unique SKU).                     |
| `Exception` (Global)              | **500 Internal Error** | Catch-all for unexpected runtime issues to prevent stack trace leakage.             |

### Sample Error Response (Validation)

_Standardized JSON response for validation failures:_

```json
{
  "type": "about:blank",
  "title": "Invalid Request Content",
  "status": 400,
  "detail": "Validation failed for request parameters",
  "instance": "/api/orders",
  "errors": {
    "quantity": "must be greater than 0",
    "productId": "must not be null"
  }
}
```

---

## ⚙️ Tech Stack

- Java 17+
- Spring Boot 4
- Spring Data JPA
- Hibernate
- PostgreSQL / MySQL
- Springdoc OpenAPI

---

## 🛠️ Configuration

The project uses a **hybrid configuration** approach:

- `application.yml`: Database and JPA settings (Structural config).
- `application.properties`: Custom application settings (Simple key-value pairs).

### 1. Database Configuration (`application.yml`)

#### Update this file with your PostgreSQL credentials.

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/your-db-name
    username: # Change to your DB user
    password: # Change to your DB password
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

### 2. Application Constants (application.properties)

#### Used for Swagger/OpenAPI URL generation.

```
application.openapi.dev-url=http://localhost:8080
application.openapi.prod-url=https://api.production.com
```

## ▶️ Running the Application

### 1. Prerequisites

Ensure your PostgreSQL database is running and matches the credentials in `application.yml`.

### 2. Start the Server

Run the standard Maven wrapper command:

```bash
./mvnw spring-boot:run
```
