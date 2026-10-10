# Hotel Booking Management System

This repository presents my software testing work for a hotel booking website: **Requirement Analysis, Test Scenarios, detailed Test Cases, API Testing with Postman and UI Automation Testing**. The testing documents describe the analysis and test design, while the Selenium/TestNG project automates selected UI scenarios.

The website comes from [khusi2707/Hotel_booking_system](https://github.com/khusi2707/Hotel_booking_system), originally built as a capstone project for the **Navikenz Internship**. Customers can browse hotels, book rooms and make payments; admins have a management dashboard. My contributions focus on the testing documents and automation tests.

**Stack:** Java 17 · Spring Boot 3 · React · H2 · JWT Auth

---

## Testing Project

The testing work includes five parts:

| Part | Purpose | Document |
| --- | --- | --- |
| Requirement Analysis | Analyze the website requirements as a basis for defining test scope | [Req-als.xlsx](docs/testing/Req-als.xlsx) |
| Test Scenarios | Organize the situations and user flows to be tested | [Test-scenario.xlsx](docs/testing/Test-scenario.xlsx) |
| Test Cases | Describe individual checks used to carry out testing | [Test-case.xlsx](docs/testing/Test-case.xlsx) |
| API Testing (Postman) | Manually exercise API scenarios using eight Postman collections and a local environment template | [API Testing README](api-tests/README.md) |
| UI Automation Testing | Automate selected scenarios using Selenium WebDriver and TestNG | [Automation README](automation-tests/README.md) |

The three Excel documents were written as part of my testing work. The workflow is **Requirement Analysis → Test Scenarios → Test Cases → UI Automation**. The automated suite covers selected cases; it does not imply that every case in the Excel documents has been automated.

### API Testing (Postman)

The API testing work includes eight Postman collections with 63 saved requests, all with configured URLs. Three unnamed empty requests were removed, and six named requests were completed from the test case document and backend source. Two login scripts store tokens; the collections currently contain no automated `pm.test` assertions. See the [API Testing README](api-tests/README.md) for import instructions, environment variables and preparation steps.

### Automation Testing (UI)

This repository includes 25 UI automation test cases built with Java 17, Selenium WebDriver, TestNG and Maven. Coverage includes authentication, hotel search/management, booking, payment and two end-to-end flows.

- [Setup, configuration and test execution](automation-tests/README.md)
- [Testing documents and automated testcase list](docs/testing/README.md)

The website is based on [khusi2707/Hotel_booking_system](https://github.com/khusi2707/Hotel_booking_system). The automation tests and QA documentation are additions to the original website project.

---

## Features

### Customer
- Register & log in with JWT-based authentication
- Browse all hotels and search by city
- View rooms with availability status
- Book a room (auto-calculated amount based on nights × rate)
- View booking history and cancel bookings
- Make payments with multiple payment methods

### Admin
- Dashboard with live stats — hotels, bookings, users, total revenue
- Full CRUD for hotels and rooms
- View and filter all bookings across all users
- Manage customer accounts
- View all payments and revenue breakdown

---

## User Stories

| ID | Epic | Story | Role |
|---|---|---|---|
| US-001 | Login Management | User Registration | Customer |
| US-002 | Login Management | User Login (JWT) | Customer / Admin |
| US-003 | Hotel Management | View & Search Hotels | Customer |
| US-004 | Hotel Management | Add Hotel | Admin |
| US-005 | Hotel Management | Update Hotel | Admin |
| US-006 | Hotel Management | Delete Hotel | Admin |
| US-007 | Room Management | View Rooms & Availability | Customer |
| US-008 | Room Management | Add Room to Hotel | Admin |
| US-009 | Room Management | Update Room | Admin |
| US-010 | Room Management | Delete Room | Admin |
| US-011 | Room Booking | Book a Room | Customer |
| US-012 | Room Booking | View Booking History | Customer |
| US-013 | Room Booking | Cancel Booking | Customer |
| US-014 | Payment Management | Make Payment | Customer |
| US-015 | Payment Management | View All Payments | Admin |
| US-016 | Customer Management | Manage Customer Accounts | Admin |
| US-017 | Admin Dashboard | View Stats & Recent Bookings | Admin |

All 17 stories are implemented end to end — see [DOCUMENTATION.md](DOCUMENTATION.md) for exactly which class handles each one.

Also in this repo:
- [TUTORIAL.md](TUTORIAL.md) — step-by-step: run the app, then use it as Admin and as Customer.
- [backend.md](backend.md) — what every backend folder/class does and why.
- [frontend.md](frontend.md) — what every frontend folder/page does and why.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.5 |
| Database | H2 (file-based, auto-created, no setup) |
| ORM | Spring Data JPA / Hibernate |
| Auth | JWT via `jjwt`, password hashing via Spring Security `BCryptPasswordEncoder` |
| API Docs | springdoc-openapi (Swagger UI) |
| Frontend | React 18, Vite 5, React Router 6 |
| HTTP Client | Axios |

---

## Project Structure

```
Hotel_booking_system/
├── backend/                         ← Java + Spring Boot REST API
│   ├── pom.xml
│   └── src/main/java/com/navikenz/hotelbooking/
│       ├── entity/                  ← JPA entities (7 tables)
│       ├── repository/              ← Spring Data JPA repositories
│       ├── dto/                     ← Request/response DTOs
│       ├── security/                ← JwtService, JwtAuthFilter
│       ├── config/                  ← SecurityConfig, OpenApiConfig
│       ├── service/ + service/impl/ ← Business logic layer
│       ├── controller/              ← REST endpoints
│       └── exception/               ← Custom exceptions + global handler
│
└── frontend/
    ├── package.json
    └── src/
        ├── context/                 ← AuthContext — global JWT state
        ├── services/                ← Axios API wrappers (one per entity)
        ├── components/              ← Navbar, ProtectedRoute
        └── pages/                   ← 13 pages (customer + admin)
```

---

## Getting Started

### Prerequisites

- Java 17 (JDK)
- Maven (or use the bundled `mvnw` wrapper — no install needed)
- Node.js 18+

### 1. Clone the repo

```bash
git clone https://github.com/khusi2707/Hotel_booking_system.git
cd Hotel_booking_system
```

### 2. Backend setup

```bash
cd backend
./mvnw spring-boot:run        # macOS / Linux
mvnw.cmd spring-boot:run      # Windows
```

Backend runs at `http://localhost:8000`
Swagger docs at `http://localhost:8000/swagger-ui/index.html`

An H2 database file (`backend/hotel_booking.mv.db`) is created automatically on first run — no setup needed.

### 3. Create the first admin account

Open `http://localhost:8000/swagger-ui/index.html`, find `POST /auth/admin/register`, and send:

```json
{
  "admin_name": "Admin",
  "email": "admin@hotel.com",
  "password": "admin123"
}
```

> Only needed once — data persists in `hotel_booking.mv.db`.

### 4. Frontend setup

Open a new terminal:

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at `http://localhost:5173`

---

## How to Use the App

Once both servers are running, open `http://localhost:5173` in your browser. The database starts empty, so follow this order:

### 1. Log in as admin
Use the admin account you created in step 3 above (e.g. `admin@hotel.com` / `admin123`). The Admin toggle on the login page must be selected.

### 2. Add a hotel
Go to **Admin → Hotels** → click **`+ Add Hotel`** → fill in hotel name, city, and avg rate/night (required fields) → **Add**.

### 3. Add rooms to that hotel
Go to **Admin → Rooms** → click **`+ Add Room`** → pick the hotel you just created, set room number, room type, and rate/day → **Add**. Repeat for as many rooms/hotels as you want.

### 4. Switch to a customer account
Log out, then click **Register** to create a customer account (or use an incognito window to stay logged in as admin at the same time).

### 5. Browse and book as a customer
- Go to **Hotels**, optionally search by city, and click into a hotel to see its rooms.
- Click **Book Now** on an available room, choose check-in/check-out dates and guest count, and confirm. The amount is calculated automatically (`rate/day × nights`).

### 6. Manage the booking
- Go to **My Bookings** to see the booking you just made.
- Click **Pay Now** to complete payment (choose any payment method — Online/Card/UPI/Cash, all are simulated).
- Click **Cancel** to cancel a confirmed booking — the room becomes available again immediately.

### 7. Check it from the admin side
Log back in as admin and check:
- **Admin → Dashboard** — total hotels, bookings, users, and revenue at a glance.
- **Admin → Bookings** — every booking across all customers, filterable by status.
- **Admin → Payments** — every payment made, with total revenue.
- **Admin → Users** — all registered customers, with the option to delete an account.

That covers the full customer and admin flow end to end.

---

## API Overview

| Group | Endpoint | Access |
|---|---|---|
| Auth | `POST /auth/register` | Public |
| Auth | `POST /auth/login` | Public |
| Auth | `POST /auth/admin/register` | Public |
| Auth | `POST /auth/admin/login` | Public |
| Hotels | `GET /hotels` · `GET /hotels/{id}` | Public |
| Hotels | `POST /PUT /DELETE /hotels` | Admin |
| Rooms | `GET /rooms` · `GET /rooms/available` | Public |
| Rooms | `POST /PUT /DELETE /rooms` | Admin |
| Bookings | `POST /bookings` · `GET /bookings/my` | Customer |
| Bookings | `PUT /bookings/{id}/cancel` | Customer |
| Bookings | `GET /bookings` | Admin |
| Payments | `POST /payments` · `GET /payments/{id}` | Customer |
| Payments | `GET /payments` | Admin |
| Users | `GET/PUT /users/{id}` | Customer |
| Users | `GET /users` · `DELETE /users/{id}` | Admin |

Full interactive docs: `http://localhost:8000/swagger-ui/index.html`

---

## Architecture

```
React Frontend (localhost:5173)
        │  JWT in Authorization header (Axios)
        ▼
Spring Boot Backend (localhost:8000)
  ┌─ Controllers ← HTTP endpoints + request validation (Bean Validation)
  ├─ Services    ← Business rules, availability checks, amount calc
  ├─ Repositories← Spring Data JPA (repository pattern)
  └─ Entities    ← JPA table definitions
        │
        ▼
H2 Database (backend/hotel_booking.mv.db)
  users · admins · hotels · room_details
  booking_details · transactions · payments
```

---

## Database Schema (Key Relationships)

```
users        ──< booking_details
hotels       ──< room_details
hotels       ──< booking_details
room_details ──< booking_details
booking_details ── payment ── transaction
```

**Auto-behaviors enforced in code:**
- Room marked **unavailable** when booked; freed again on cancellation
- Booking `amount` = `rate_per_day × number of nights` (calculated server-side)
- Payment creation also writes a `Transaction` record in the same call

---

## Configuration

All configuration lives in `backend/src/main/resources/application.properties` — JWT secret/expiry, H2 datasource, server port, Swagger paths. No `.env` file or manual setup required; sensible defaults are already checked in for local development.

---

## Design Patterns

| Pattern | Where used |
|---|---|
| Layered Architecture | Controller → Service → Repository → Entity |
| Repository Pattern | Spring Data JPA repositories — single source for all DB queries |
| DTO Pattern | `dto/` — separates API shape from JPA entities |
| Dependency Injection | Plain constructor injection (`@Autowired`) throughout — no Lombok |
| Global Exception Handler | `@RestControllerAdvice` maps exceptions to consistent HTTP responses |
| Context API | `AuthContext.jsx` — global JWT state across all React pages |
| Interceptor Pattern | `api.js` — Axios auto-attaches JWT to every request |

---

*Navikenz Internship Capstone — Hotel Booking Management System*
