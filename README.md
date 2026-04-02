# Finance Data Processing and Access Control Backend

A full-stack finance dashboard built with **Spring Boot (Java 17)** and **React + Vite**, featuring JWT authentication, role-based access control, financial record management, and dashboard analytics.

---

## Tech Stack

| Layer     | Technology                                      |
|-----------|-------------------------------------------------|
| Backend   | Java 17, Spring Boot 3.2, Spring Security, JPA  |
| Database  | H2 (file-based, zero-config)                    |
| Auth      | JWT (jjwt 0.12)                                 |
| Validation| Jakarta Bean Validation + Spring @Valid          |
| Frontend  | React 18, Vite, React Router 6, Recharts        |
| Styling   | CSS Modules                                     |

---

## Project Structure

```
finance-app/
├── backend/
│   ├── pom.xml
│   └── src/main/java/com/finance/
│       ├── FinanceApplication.java
│       ├── config/
│       │   ├── SecurityConfig.java       # Spring Security + CORS
│       │   └── DataSeeder.java           # Demo data on first run
│       ├── controller/
│       │   ├── AuthController.java       # POST /api/auth/register, /login
│       │   ├── UserController.java       # GET/PATCH/DELETE /api/users/**
│       │   ├── RecordController.java     # CRUD /api/records/**
│       │   └── DashboardController.java  # GET /api/dashboard/summary
│       ├── dto/                          # Request/Response DTOs
│       ├── entity/
│       │   ├── User.java                 # Implements UserDetails
│       │   └── FinancialRecord.java      # Soft-delete support
│       ├── exception/                    # GlobalExceptionHandler + custom exceptions
│       ├── repository/
│       │   ├── UserRepository.java
│       │   ├── FinancialRecordRepository.java
│       │   └── RecordSpecification.java  # Dynamic filter queries
│       ├── security/
│       │   ├── JwtUtil.java              # Token generation + validation
│       │   └── JwtAuthFilter.java        # OncePerRequestFilter
│       └── service/
│           ├── AuthService.java          # UserDetailsService impl
│           ├── UserService.java
│           ├── RecordService.java
│           └── DashboardService.java     # Aggregation queries
└── frontend/
    ├── src/
    │   ├── api/
    │   │   ├── client.js                 # Axios instance with JWT interceptor
    │   │   └── services.js               # API helper functions
    │   ├── context/AuthContext.jsx       # Auth state management
    │   ├── components/
    │   │   ├── layout/Layout.jsx         # Sidebar + nav
    │   │   └── common/Spinner.jsx
    │   └── pages/
    │       ├── LoginPage.jsx
    │       ├── DashboardPage.jsx         # KPI cards + charts
    │       ├── RecordsPage.jsx           # Filter + CRUD table
    │       └── UsersPage.jsx             # Admin user management
    └── vite.config.js                    # Proxy /api → localhost:8080
```

---

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- Node.js 18+

---

### 1. Run the Backend

```bash
cd backend
mvn spring-boot:run
```

The server starts on **http://localhost:8080**.

On first run, `DataSeeder` automatically creates three demo accounts and sample financial records:

| Email                   | Password    | Role    |
|-------------------------|-------------|---------|
| admin@finance.com       | admin123    | ADMIN   |
| analyst@finance.com     | analyst123  | ANALYST |
| viewer@finance.com      | viewer123   | VIEWER  |

H2 database is stored at `./data/financedb` (file-based, persists across restarts).

**H2 Console** (development): http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:file:./data/financedb`
- Username: `sa` | Password: _(leave blank)_

---

### 2. Run the Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend starts on **http://localhost:5173** and proxies `/api/*` to the backend.

---

### 3. Run Tests

```bash
cd backend
mvn test
```

8 integration tests covering auth, role enforcement, and dashboard access.

---

## API Reference

### Authentication

```
POST /api/auth/register    Create account (public)
POST /api/auth/login       Get JWT token (public)
```

**Login response:**
```json
{
  "token": "eyJ...",
  "user": { "id": 1, "name": "Admin", "email": "...", "role": "ADMIN", "status": "ACTIVE" }
}
```

Include the token in all subsequent requests:
```
Authorization: Bearer <token>
```

---

### Users  _(ADMIN only)_

```
GET    /api/users          List all users
GET    /api/users/me       Get own profile (any role)
GET    /api/users/{id}     Get user by id
PATCH  /api/users/{id}     Update name / role / status
DELETE /api/users/{id}     Hard delete user
```

**PATCH body (all fields optional):**
```json
{ "name": "New Name", "role": "ANALYST", "status": "INACTIVE" }
```

---

### Financial Records

| Method | Endpoint          | Roles Allowed     |
|--------|-------------------|-------------------|
| GET    | /api/records      | ANALYST, ADMIN    |
| GET    | /api/records/{id} | ANALYST, ADMIN    |
| POST   | /api/records      | ADMIN             |
| PATCH  | /api/records/{id} | ADMIN             |
| DELETE | /api/records/{id} | ADMIN (soft-delete)|

**GET /api/records query parameters:**

| Param     | Type   | Example        | Description             |
|-----------|--------|----------------|-------------------------|
| type      | string | INCOME         | Filter by type          |
| category  | string | Salary         | Partial match           |
| date_from | date   | 2024-01-01     | Start date (inclusive)  |
| date_to   | date   | 2024-12-31     | End date (inclusive)    |
| page      | int    | 0              | Zero-based page number  |
| size      | int    | 20             | Page size               |
| sort_by   | string | date           | Field to sort by        |
| sort_dir  | string | desc           | asc or desc             |

**POST/PATCH body:**
```json
{
  "amount":   1500.00,
  "type":     "INCOME",
  "category": "Salary",
  "date":     "2024-03-01",
  "notes":    "March salary"
}
```

---

### Dashboard  _(All authenticated users)_

```
GET /api/dashboard/summary
```

**Response:**
```json
{
  "totalIncome":   28750.00,
  "totalExpenses": 2095.00,
  "netBalance":    26655.00,
  "incomeByCategory":  { "Salary": 25500, "Freelance": 1200, ... },
  "expenseByCategory": { "Rent": 1390, "Utilities": 215, ... },
  "recentActivity": [ ... ],
  "monthlyTrends": [
    { "month": "2024-01", "income": 9200, "expense": 575 },
    ...
  ]
}
```

---

## Role-Based Access Control

| Action                         | VIEWER | ANALYST | ADMIN |
|--------------------------------|--------|---------|-------|
| View dashboard summary         | ✅     | ✅      | ✅    |
| List / view financial records  | ❌     | ✅      | ✅    |
| Create financial records       | ❌     | ❌      | ✅    |
| Update financial records       | ❌     | ❌      | ✅    |
| Delete financial records       | ❌     | ❌      | ✅    |
| List / view users              | ❌     | ❌      | ✅    |
| Update user role / status      | ❌     | ❌      | ✅    |
| Delete users                   | ❌     | ❌      | ✅    |

Access control is enforced via Spring Security's `@PreAuthorize` annotations on each controller method. Unauthorized requests receive a `403 Forbidden` response.

---

## Validation & Error Handling

All inputs are validated using Jakarta Bean Validation (`@NotNull`, `@NotBlank`, `@Email`, `@Size`, `@DecimalMin`).

**Validation error response (HTTP 400):**
```json
{
  "timestamp": "2024-03-01T10:00:00",
  "status": 400,
  "error": "Validation failed",
  "details": {
    "amount":   "Amount must be greater than 0",
    "category": "Category is required"
  }
}
```

**Error response (other errors):**
```json
{
  "timestamp": "2024-03-01T10:00:00",
  "status": 404,
  "error": "Record not found: 99"
}
```

HTTP status codes used:
- `200 OK` — successful read/update
- `201 Created` — successful creation
- `204 No Content` — successful delete
- `400 Bad Request` — validation failure / invalid operation
- `401 Unauthorized` — missing or invalid JWT
- `403 Forbidden` — insufficient role
- `404 Not Found` — resource does not exist

---

## Design Decisions & Assumptions

### Database Choice: H2 (file-based)
H2 was chosen for zero-config local development. The `spring.datasource.url` can be replaced with any JDBC URL (PostgreSQL, MySQL) without changing application code — JPA handles the abstraction.

### Soft Delete
`DELETE /api/records/{id}` marks the record as `deleted = true` rather than removing it from the database. This preserves audit history and allows potential recovery. All queries filter `WHERE deleted = false`.

### JWT Expiry
Tokens expire after 8 hours (`app.jwt.expiration-ms=28800000`). There is no refresh token endpoint — the user must log in again after expiry. This is acceptable for an internal dashboard.

### Admin Self-Protection
Admins cannot demote their own role or deactivate their own account. This prevents accidental lockout.

### Role Model
Three roles were implemented as specified: VIEWER (dashboard read-only), ANALYST (records read + dashboard), ADMIN (full access). Roles are stored as ENUMs and enforced both at the controller level (`@PreAuthorize`) and reflected in the frontend navigation.

### Dynamic Filtering
Record filtering uses Spring Data JPA Specifications (`RecordSpecification`) allowing any combination of `type`, `category`, `date_from`, and `date_to` filters in a single, composable query — avoids a combinatorial explosion of repository methods.

### Frontend Proxy
Vite's dev server proxies `/api/*` to `localhost:8080`, so the React app never hard-codes a backend URL. In production, a reverse proxy (nginx) would serve the built frontend and forward `/api` to the Spring Boot process.

---

## Optional Enhancements Included

- ✅ JWT authentication (8-hour tokens)
- ✅ Pagination for record listing (page, size, sort)
- ✅ Category-based search / filtering
- ✅ Soft delete for financial records
- ✅ Integration tests (8 test cases)
- ✅ Comprehensive API documentation (this README)
- ✅ H2 console for database inspection
- ✅ Demo data seeded on first run
# finance-app
