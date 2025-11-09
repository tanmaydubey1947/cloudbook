# CloudBook - Bookstore Catalog and Order Management Service

## ✨ Features

- **User Authentication & Authorization**: JWT-based authentication with role-based access control (ADMIN, CUSTOMER)
- **Catalog Management**: CRUD operations for books with filtering, pagination, and stock management
- **Shopping Cart**: Add, remove, and manage items in cart
- **Order Management**: Place orders, view order history, and cancel orders
- **Analytics**: Sales summary and top-selling books analytics for admins
- **Resilience**: Circuit breaker and retry patterns for stock updates
- **API Documentation**: Swagger/OpenAPI integration
- **H2 Database**: In-memory database for development

## 🛠 Tech Stack

- **Framework**: Spring Boot 3.4.4
- **Language**: Java 17
- **Security**: Spring Security with JWT
- **Database**: H2 (In-memory)
- **ORM**: Spring Data JPA
- **Resilience**: Resilience4j (Circuit Breaker, Retry, Rate Limiter)
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Build Tool**: Maven
- **Containerization**: Docker
- **Orchestration**: Kubernetes

## 📦 Prerequisites

- Java 17 or higher
- Maven 3.6+ (or use included Maven Wrapper)

## 🚀 Setup Instructions

### Local Development

1. **Clone the repository**
   ```bash
   git clone https://github.com/tanmaydubey1947/cloudbook
   cd cloudbook
   ```

2. **Build the project**
   ```bash
   # Using Maven Wrapper (Windows)
   .\mvnw.cmd clean install
   
   # Using Maven Wrapper (Linux/Mac)
   ./mvnw clean install
   
   # Or using Maven directly
   mvn clean install
   ```

3. **Run the application**
   ```bash
   # Using Maven Wrapper
   .\mvnw.cmd spring-boot:run
   
   # Or using Maven
   mvn spring-boot:run
   
   # Or run the JAR directly
   java -jar target/cloudbook-0.0.1-SNAPSHOT.jar
   ```

4. **Access the application**
   - Application: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui/index.html
   - H2 Console: http://localhost:8080/h2-console
     - JDBC URL: `jdbc:h2:mem:cloudbookdb`
     - Username: `sa`
     - Password: `123`

### Docker

1. **Build the Docker image**
   ```bash
   # First, build the JAR
   mvn clean package
   
   # Build Docker image
   docker build -t cloudbook:latest .
   ```

2. **Run the container**
   ```bash
   docker run -p 8080:8080 cloudbook:latest
   ```

3. **Access the application**
   - Application: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui/index.html

### Kubernetes

1. **Apply Kubernetes manifests**
   ```bash
   # Apply PersistentVolume
   kubectl apply -f k8s/pv.yaml
   
   # Apply PersistentVolumeClaim
   kubectl apply -f k8s/pvc.yaml
   
   # Apply Deployment
   kubectl apply -f k8s/deployment.yaml
   
   # Apply Service
   kubectl apply -f k8s/service.yaml
   ```

2. **Check deployment status**
   ```bash
   kubectl get pods
   kubectl get services
   ```

3. **Port forward to access the service**
   ```bash
   kubectl port-forward service/cloudbook-service 8080:8080
   ```

## ⚙️ Configuration

Key configuration in `application.properties`:

```properties
# Server
server.port=8080

# JWT
jwt.expiration=3600000  # 1 hour in milliseconds
jwt.secret=aK8O44ZO3WntAHO+ArNlxt3d5y87cwqj1C7uZBpJ9I0=

# H2 Database
spring.datasource.url=jdbc:h2:mem:cloudbookdb
spring.datasource.username=sa
spring.datasource.password=123
spring.h2.console.enabled=true

# Resilience4j
resilience4j.retry.instances.stockUpdateRetry.max-attempts=3
resilience4j.retry.instances.stockUpdateRetry.wait-duration=500ms
resilience4j.ratelimiter.instances.stockUpdateRate.limit-for-period=5
resilience4j.ratelimiter.instances.stockUpdateRate.limit-refresh-period=10s
```

## 📡 API Endpoints & Sample Requests

### Authentication APIs

#### 1. Register User

**Endpoint**: `POST /api/auth/register`

**Authorization**: Not required

**Request Body**:
```json
{
  "username": "john_doe",
  "password": "password123",
  "role": "CUSTOMER"
}
```

**cURL**:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123",
    "role": "CUSTOMER"
  }'
```

**Response** (200 OK):
```json
{
  "message": "User registered successfully"
}
```

#### 2. Login (Get JWT Token)

**Endpoint**: `POST /api/auth/login`

**Authorization**: Not required

**Request Body**:
```json
{
  "username": "john_doe",
  "password": "password123"
}
```

**cURL**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123"
  }'
```

**Response** (201 Created):
```json
{
  "message": "Token generated successfully",
  "msg": "Authentication successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600000,
  "refreshToken": null
}
```

**Note**: Save the `token` value for subsequent authenticated requests.

---

### Catalog APIs

#### 3. Get All Books (with Pagination & Filters)

**Endpoint**: `GET /api/books`

**Authorization**: Required (ADMIN or CUSTOMER)

**Query Parameters**:
- `genre` (optional): Filter by genre
- `author` (optional): Filter by author
- `minPrice` (optional): Minimum price filter
- `maxPrice` (optional): Maximum price filter
- `page` (default: 0): Page number
- `size` (default: 10): Page size

**cURL**:
```bash
# Get all books (first page)
curl -X GET "http://localhost:8080/api/books?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Filter by genre
curl -X GET "http://localhost:8080/api/books?genre=Fiction&page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Filter by price range
curl -X GET "http://localhost:8080/api/books?minPrice=10&maxPrice=50&page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response** (200 OK):
```json
{
  "content": [
    {
      "message": null,
      "bookId": "550e8400-e29b-41d4-a716-446655440000",
      "title": "The Great Gatsby",
      "author": "F. Scott Fitzgerald",
      "price": 12.99
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1
}
```

#### 4. Get Book by ID

**Endpoint**: `GET /api/books/{bookId}`

**Authorization**: Required (ADMIN or CUSTOMER)

**cURL**:
```bash
curl -X GET "http://localhost:8080/api/books/550e8400-e29b-41d4-a716-446655440000" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response** (200 OK):
```json
{
  "message": null,
  "bookId": "550e8400-e29b-41d4-a716-446655440000",
  "title": "The Great Gatsby",
  "author": "F. Scott Fitzgerald",
  "price": 12.99
}
```

#### 5. Add New Book

**Endpoint**: `POST /api/books`

**Authorization**: Required (ADMIN only)

**Request Body**:
```json
{
  "title": "The Great Gatsby",
  "author": "F. Scott Fitzgerald",
  "genre": "Fiction",
  "price": 12.99,
  "stock": 100,
  "rating": 4.5
}
```

**cURL**:
```bash
curl -X POST http://localhost:8080/api/books \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "The Great Gatsby",
    "author": "F. Scott Fitzgerald",
    "genre": "Fiction",
    "price": 12.99,
    "stock": 100,
    "rating": 4.5
  }'
```

**Response** (201 Created):
```json
{
  "message": "Book added successfully",
  "bookId": "550e8400-e29b-41d4-a716-446655440000",
  "title": "The Great Gatsby",
  "author": "F. Scott Fitzgerald",
  "price": 12.99
}
```

#### 6. Update Book

**Endpoint**: `PUT /api/books/{bookId}`

**Authorization**: Required (ADMIN only)

**Request Body**:
```json
{
  "title": "The Great Gatsby (Updated)",
  "author": "F. Scott Fitzgerald",
  "genre": "Fiction",
  "price": 14.99,
  "stock": 150,
  "rating": 4.7
}
```

**cURL**:
```bash
curl -X PUT "http://localhost:8080/api/books/550e8400-e29b-41d4-a716-446655440000" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "The Great Gatsby (Updated)",
    "author": "F. Scott Fitzgerald",
    "genre": "Fiction",
    "price": 14.99,
    "stock": 150,
    "rating": 4.7
  }'
```

**Response** (200 OK):
```json
{
  "message": "Book updated successfully",
  "bookId": "550e8400-e29b-41d4-a716-446655440000",
  "title": "The Great Gatsby (Updated)",
  "author": "F. Scott Fitzgerald",
  "price": 14.99
}
```

#### 7. Delete Book

**Endpoint**: `DELETE /api/books/{bookId}`

**Authorization**: Required (ADMIN only)

**cURL**:
```bash
curl -X DELETE "http://localhost:8080/api/books/550e8400-e29b-41d4-a716-446655440000" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response** (200 OK):
```json
{
  "message": "Book deleted successfully",
  "bookId": "550e8400-e29b-41d4-a716-446655440000",
  "title": "The Great Gatsby",
  "author": "F. Scott Fitzgerald",
  "price": 12.99
}
```

#### 8. Update Book Stock

**Endpoint**: `PATCH /api/books/{bookId}/stock`

**Authorization**: Required (ADMIN only)

**Request Body**:
```json
{
  "delta": 50
}
```

**cURL**:
```bash
curl -X PATCH "http://localhost:8080/api/books/550e8400-e29b-41d4-a716-446655440000/stock" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "delta": 50
  }'
```

**Response** (200 OK):
```json
{
  "message": "Stock updated successfully",
  "bookId": "550e8400-e29b-41d4-a716-446655440000",
  "title": "The Great Gatsby",
  "author": "F. Scott Fitzgerald",
  "price": 12.99
}
```

---

### Cart APIs

#### 9. View Cart

**Endpoint**: `GET /api/cart`

**Authorization**: Required (CUSTOMER only)

**cURL**:
```bash
curl -X GET http://localhost:8080/api/cart \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response** (200 OK):
```json
{
  "message": "Cart retrieved successfully",
  "items": [
    {
      "bookId": "550e8400-e29b-41d4-a716-446655440000",
      "title": "The Great Gatsby",
      "quantity": 2,
      "price": 12.99,
      "subtotal": 25.98
    }
  ],
  "total": 25.98
}
```

#### 10. Add to Cart

**Endpoint**: `POST /api/cart/add`

**Authorization**: Required (CUSTOMER only)

**Request Body**:
```json
{
  "bookId": "550e8400-e29b-41d4-a716-446655440000",
  "quantity": 2
}
```

**cURL**:
```bash
curl -X POST http://localhost:8080/api/cart/add \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "bookId": "550e8400-e29b-41d4-a716-446655440000",
    "quantity": 2
  }'
```

**Response** (201 Created):
```json
{
  "message": "Item added to cart successfully"
}
```

#### 11. Remove Item from Cart

**Endpoint**: `DELETE /api/cart/remove/{bookId}`

**Authorization**: Required (CUSTOMER only)

**cURL**:
```bash
curl -X DELETE "http://localhost:8080/api/cart/remove/550e8400-e29b-41d4-a716-446655440000" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response** (200 OK):
```json
{
  "message": "Item removed from cart successfully"
}
```

#### 12. Clear Cart

**Endpoint**: `DELETE /api/cart/clear`

**Authorization**: Required (CUSTOMER only)

**cURL**:
```bash
curl -X DELETE http://localhost:8080/api/cart/clear \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response** (200 OK):
```json
{
  "message": "Cart cleared successfully"
}
```

---

### Order APIs

#### 13. Create Order

**Endpoint**: `POST /api/orders`

**Authorization**: Required (CUSTOMER only)

**Note**: Creates an order from the current user's cart

**cURL**:
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response** (201 Created):
```json
{
  "message": "Order placed successfully",
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "items": [
    {
      "bookId": "550e8400-e29b-41d4-a716-446655440000",
      "title": "The Great Gatsby",
      "quantity": 2,
      "priceAtPurchase": 12.99
    }
  ],
  "totalAmount": 25.98,
  "orderDate": "2024-01-15T10:30:00",
  "status": "PENDING"
}
```

#### 14. Get Order by ID

**Endpoint**: `GET /api/orders/{orderId}`

**Authorization**: Required (CUSTOMER only)

**cURL**:
```bash
curl -X GET "http://localhost:8080/api/orders/660e8400-e29b-41d4-a716-446655440001" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response** (200 OK):
```json
{
  "message": "Order retrieved successfully",
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "items": [
    {
      "bookId": "550e8400-e29b-41d4-a716-446655440000",
      "title": "The Great Gatsby",
      "quantity": 2,
      "priceAtPurchase": 12.99
    }
  ],
  "totalAmount": 25.98,
  "orderDate": "2024-01-15T10:30:00",
  "status": "PENDING"
}
```

#### 15. Get User Orders

**Endpoint**: `GET /api/orders`

**Authorization**: Required (CUSTOMER only)

**cURL**:
```bash
curl -X GET http://localhost:8080/api/orders \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response** (200 OK):
```json
{
  "message": "Orders retrieved successfully",
  "data": [
    {
      "id": "660e8400-e29b-41d4-a716-446655440001",
      "items": [],
      "totalAmount": 25.98,
      "orderDate": "2024-01-15T10:30:00",
      "status": "PENDING"
    }
  ]
}
```

#### 16. Cancel Order

**Endpoint**: `PATCH /api/orders/{orderId}/cancel`

**Authorization**: Required (CUSTOMER only)

**cURL**:
```bash
curl -X PATCH "http://localhost:8080/api/orders/660e8400-e29b-41d4-a716-446655440001/cancel" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response** (204 No Content)

---

### Analytics APIs

#### 17. Get Top Books

**Endpoint**: `GET /api/admin/analytics/top-books`

**Authorization**: Required (ADMIN only)

**cURL**:
```bash
curl -X GET http://localhost:8080/api/admin/analytics/top-books \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response** (200 OK):
```json
[
  {
    "bookId": "550e8400-e29b-41d4-a716-446655440000",
    "title": "The Great Gatsby",
    "author": "F. Scott Fitzgerald",
    "totalSales": 150,
    "revenue": 1948.50
  }
]
```

#### 18. Get Sales Summary

**Endpoint**: `GET /api/admin/analytics/sales-summary`

**Authorization**: Required (ADMIN only)

**cURL**:
```bash
curl -X GET http://localhost:8080/api/admin/analytics/sales-summary \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response** (200 OK):
```json
{
  "totalOrders": 50,
  "totalRevenue": 1250.75,
  "averageOrderValue": 25.02,
  "totalBooksSold": 200
}
```

---


## 🗄️ Database

The application uses **H2 in-memory database** for development. 

- **H2 Console**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:cloudbookdb`
- **Username**: `sa`
- **Password**: `123`

**Note**: Data is lost when the application restarts.

## 🛡️ Resilience Patterns

The application implements Resilience4j patterns:

- **Retry**: Stock update operations retry up to 3 times with 500ms delay
- **Rate Limiter**: Stock updates limited to 5 requests per 10 seconds
- **Circuit Breaker**: Protects against cascading failures