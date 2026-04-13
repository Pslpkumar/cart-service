# Cart Service

A Spring Boot microservice for managing shopping carts. Communicates with
Product Service via WebClient and publishes cart events to Kafka.

## Tech Stack
- Java 17
- Spring Boot 3.2.3
- Spring WebFlux (WebClient)
- Spring Data JPA
- Spring Kafka (Producer)
- SQL Server
- Lombok
- Maven

## Architecture Role
- **REST API** — exposes cart endpoints on port `8082`
- **Kafka Producer** — publishes `ITEM_ADDED` events to `cart-events` topic
- **WebClient** — calls Product Service to fetch product info and validate stock
- **Async Processing** — parallel stock validation using CompletableFuture

## Prerequisites
- Java 17+
- Maven 3.8+
- SQL Server (local or Docker)
- Kafka running on `localhost:29092`
- Product Service running on `localhost:8081`

## Database Setup
Connect to SQL Server and run:
```sql
CREATE DATABASE CartDB;

USE CartDB;
CREATE TABLE carts (
    id      INT IDENTITY(1,1) PRIMARY KEY,
    user_id NVARCHAR(255) NOT NULL
);

CREATE TABLE cart_items (
    id         INT IDENTITY(1,1) PRIMARY KEY,
    cart_id    INT NOT NULL,
    product_id INT NOT NULL,
    quantity   INT NOT NULL,
    CONSTRAINT fk_cart FOREIGN KEY (cart_id) REFERENCES carts(id)
);
```

## Configuration
Edit `src/main/resources/application.properties`:
```properties
server.port=8082
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=CartDB;encrypt=false;trustServerCertificate=true
spring.datasource.username=username
spring.datasource.password=YourStrong@Passw0rd
spring.jpa.hibernate.ddl-auto=validate
spring.kafka.bootstrap-servers=localhost:29092
product.service.base-url=http://localhost:8081
kafka.topic.cart-events=cart-events
```

## Run
```bash
mvn clean install
mvn spring-boot:run
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST   | /api/cart/add | Add item to cart |
| GET    | /api/cart/{userId} | Get cart for user |
| DELETE | /api/cart/{userId}/clear | Clear entire cart |

## Kafka
- **Role:** Producer
- **Topic:** `cart-events`
- **Event Type:** `ITEM_ADDED`
- **Trigger:** Every successful `addToCart()` call

## Communication Flow
