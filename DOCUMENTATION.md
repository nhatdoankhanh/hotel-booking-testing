# Hotel Booking Management System — Documentation
### Navikenz Internship — Capstone Project

This document  walks through each of the 17 user stories one at a time: what it does, how to try it yourself, and exactly which file/method makes it work.

**Stack:** Java 17 + Spring Boot 3.5 (backend) · React 18 (frontend) · H2 database (file-based, zero setup) · JWT authentication

**Status:** ✅ Complete — all 17 user stories are implemented and were manually re-verified against the running API before this was written.

---

## Table of Contents

1. [What This Project Is](#1-what-this-project-is)
2. [Quick Start](#2-quick-start)
3. [Project Structure](#3-project-structure)
4. [Architecture at a Glance](#4-architecture-at-a-glance)
5. [The 17 User Stories, Step by Step](#5-the-17-user-stories-step-by-step)
6. [Data Model Reference](#6-data-model-reference)
7. [Full API Reference](#7-full-api-reference)
8. [Authentication Flow](#8-authentication-flow)
9. [Design Patterns Used](#9-design-patterns-used)
10. [Troubleshooting](#10-troubleshooting)
11. [Testing Checklist (Manual QA)](#11-testing-checklist-manual-qa)

---

## 1. What This Project Is

A **Hotel Booking Management System** — the kind of thing you'd see behind a small hotel chain's website. There are two kinds of people who use it:

- **Customers** — browse hotels, look at rooms, book one for a date range, pay for it, and see/cancel their bookings.
- **Admins** — add and manage hotels and rooms, look at every booking and payment across all customers, and manage customer accounts.

Everything is protected by **JWT login** — a customer token and an admin token can do different things, enforced on the server, not just hidden in the UI.

The **backend** (Java, Spring Boot) is a REST API. The **frontend** (React) is a single-page app that talks to it over HTTP. They're two separate programs that run side by side on your machine during development.

---

## 2. Quick Start

> **You need:** JDK 17 and Node.js 18+ installed. Nothing else — no Maven install (a wrapper script is included) and no database install (H2 creates its own file).

### Step 1 — Start the backend

```bash
cd backend                    # from inside Hotel_Booking_system
.\mvnw.cmd spring-boot:run    # Windows (PowerShell) — the .\ is required, mvnw.cmd isn't on PATH
mvnw.cmd spring-boot:run      # Windows (cmd.exe)
./mvnw spring-boot:run        # macOS / Linux
```

Wait for a line like `Started HotelbookingApplication in 5 seconds`. The API is now live at `http://localhost:8000`, and a database file `backend/hotel_booking.mv.db` has been created automatically.

### Step 2 — Open the interactive API docs

Go to `http://localhost:8000/swagger-ui/index.html`. This page lists every endpoint and lets you send real requests from the browser — you'll use it in Step 4.

### Step 3 — Start the frontend

In a **second terminal**:

```bash
cd Hotel_Booking_system/frontend
npm install     # first time only
npm run dev
```

Open `http://localhost:5173`.

### Step 4 — Create your first admin account

Nobody is an admin yet — you have to make one. In Swagger, find `POST /auth/admin/register` → **Try it out** → send:

```json
{
  "admin_name": "Admin",
  "email": "admin@hotel.com",
  "password": "admin123"
}
```

You now have an admin account. Section 5 below walks you through using it.

---

## 3. Project Structure

```
Hotel_Booking_system/
├── backend/                                       Java + Spring Boot REST API
│   ├── pom.xml                                    Maven build file — all dependencies
│   └── src/main/java/com/navikenz/hotelbooking/
│       ├── entity/          7 classes, one per database table
│       ├── repository/      7 interfaces — Spring Data JPA, no SQL written by hand
│       ├── dto/             Request/response shapes, grouped by feature
│       ├── security/        JwtService (sign/verify tokens), JwtAuthFilter (checks every request)
│       ├── config/          SecurityConfig (who can call what), OpenApiConfig (Swagger)
│       ├── service/         Business rules — one interface + one impl per feature
│       ├── controller/      The actual REST endpoints
│       └── exception/       Turns errors into clean {"detail": "..."} JSON responses
│
└── frontend/                                      React single-page app
    └── src/
        ├── context/AuthContext.jsx                Holds the logged-in user + token
        ├── services/                              One file per feature, wraps Axios calls
        ├── components/common/                     Navbar, route guards
        └── pages/                                 13 screens (7 customer, 6 admin)
```

**Rule of thumb for the backend:** a request always flows in one direction —
`controller` (reads the HTTP request) → `service` (applies the business rule) → `repository` (talks to the database) → back up with a `dto` as the response. Nothing skips a layer.

---

## 4. Architecture at a Glance

```
  Browser (React, localhost:5173)
       │  fetch/axios call, with "Authorization: Bearer <token>" if logged in
       ▼
  Spring Boot API (localhost:8000)
       │
       ├─ JwtAuthFilter          reads the token on every request, figures out who you are
       ├─ SecurityConfig         decides if "who you are" is allowed to hit this URL
       ├─ Controller             receives the validated request
       ├─ Service                the actual business logic (the interesting part)
       └─ Repository             reads/writes the database
       │
       ▼
  H2 database file (backend/hotel_booking.mv.db)
```

If a request fails a rule (wrong password, room already booked, not an admin...), the service layer throws an exception, and `GlobalExceptionHandler` turns it into a JSON error with the right HTTP status code — the controller code itself never has to think about error formatting.

---

## 5. The 17 User Stories, Step by Step

This section is a guided walkthrough. If you follow it **in order on a fresh database**, the IDs shown (`hotel_id: 1`, `booking_id: 1`, etc.) will match what you get back — it's one continuous example, not 17 disconnected snippets.

Every `curl` example assumes the backend is running at `http://localhost:8000`. You can run the exact same steps through Swagger UI instead if you prefer clicking to typing.

---

### US-001 — User Registration
**Epic:** Login Management · **Role:** Customer · **Auth needed:** None

**What it does:** Anyone can create a customer account with a name, email, and password.

**Try it:**
```bash
curl -X POST http://localhost:8000/auth/register \
  -H "Content-Type: application/json" \
  -d '{"user_name":"John Doe","email":"john@test.com","password":"pass123"}'
```

**What happens behind the scenes:**
1. `AuthController.register()` receives the request.
2. `UserServiceImpl.registerUser()` checks the email isn't already taken — if it is, you get `400 "Email already registered"`.
3. The password is hashed with `BCryptPasswordEncoder` — it is **never** stored as plain text.
4. A new row is saved in the `users` table with `role = "customer"`.

**You get back:**
```json
{"message": "Registration successful", "user_id": 1, "email": "john@test.com"}
```

**Acceptance criteria met:** ✅ account created, email saved, duplicate email rejected.

---

### US-002 — User Login
**Epic:** Login Management · **Role:** Customer / Admin · **Auth needed:** None

**What it does:** Exchanges an email + password for a JWT token, which every future request will carry.

**Try it (customer):**
```bash
curl -X POST http://localhost:8000/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@test.com","password":"pass123"}'
```

**Try it (admin — same idea, different endpoint):**
```bash
curl -X POST http://localhost:8000/auth/admin/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@hotel.com","password":"admin123"}'
```

**What happens behind the scenes:**
1. `UserServiceImpl.login()` (or `AdminServiceImpl.login()` for admins) looks up the account by email.
2. `BCryptPasswordEncoder.matches()` checks the password against the stored hash.
3. If it matches, `JwtService.generateToken()` builds a signed token containing `sub` (the user's ID) and `role` (`customer` or `admin`), valid for 24 hours.

**You get back:**
```json
{"access_token": "eyJhbGciOiJIUzM4NCJ9...", "token_type": "bearer", "role": "customer", "user_id": 1, "user_name": "John Doe"}
```

Save the `access_token` — every request from here on needs it in an `Authorization: Bearer <token>` header.

**Acceptance criteria met:** ✅ correct credentials return a JWT; wrong ones return `401`.

---

### US-003 — View Hotels
**Epic:** Hotel Management · **Role:** Customer · **Auth needed:** None (public)

**What it does:** Lists every hotel, optionally filtered by city.

**Try it:**
```bash
curl http://localhost:8000/hotels
curl "http://localhost:8000/hotels?city=Mumbai"
```

**Behind the scenes:** `HotelController.getAllHotels()` calls `HotelServiceImpl.searchHotels(city)` when `?city=` is present (a case-insensitive substring match), or `getAllHotels()` otherwise.

**Acceptance criteria met:** ✅ all hotels listed; city search works and ignores case.

---

### US-004 — Add Hotel
**Epic:** Hotel Management · **Role:** Admin · **Auth needed:** Admin token

**What it does:** Creates a new hotel. This is the first thing an admin does before anything else can happen.

**Try it** (replace `<ADMIN_TOKEN>` with the token from US-002):
```bash
curl -X POST http://localhost:8000/hotels \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -d '{"city":"Mumbai","hotel_name":"Taj Palace","avg_rate_per_day":5000}'
```

**Behind the scenes:** `HotelController.addHotel()` requires the `ROLE_ADMIN` authority (enforced in `SecurityConfig` before the request even reaches the controller) → `HotelServiceImpl.addHotel()` saves it.

**You get back:**
```json
{"hotel_id": 1, "city": "Mumbai", "hotel_name": "Taj Palace", "avg_rate_per_day": 5000.0, ...}
```

**Acceptance criteria met:** ✅ hotel appears in `GET /hotels` immediately after creation; a customer token gets `403`.

---

### US-005 — Update Hotel
**Epic:** Hotel Management · **Role:** Admin

**Try it:**
```bash
curl -X PUT http://localhost:8000/hotels/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -d '{"avg_rate_per_day":5500}'
```

**Behind the scenes:** `HotelServiceImpl.updateHotel()` only overwrites the fields you send (a partial update) and returns `404` if the hotel doesn't exist.

**Acceptance criteria met:** ✅ changed fields are updated in the database; unspecified fields are left alone.

---

### US-006 — Delete Hotel
**Epic:** Hotel Management · **Role:** Admin

**Try it:**
```bash
curl -X DELETE http://localhost:8000/hotels/1 -H "Authorization: Bearer <ADMIN_TOKEN>"
```

**Behind the scenes:** `HotelServiceImpl.removeHotel()` first calls `RoomRepository.deleteByHotelId()` to delete every room that belongs to the hotel, **then** deletes the hotel itself — so you never end up with orphaned rooms pointing at a hotel that no longer exists.

**Acceptance criteria met:** ✅ hotel and all its rooms disappear from their respective listings.

> The rest of this walkthrough assumes hotel `1` ("Taj Palace") still exists — don't actually delete it if you're following along end-to-end.

---

### US-007 — View Rooms
**Epic:** Room Management · **Role:** Customer · **Auth needed:** None (public)

**Try it:**
```bash
curl "http://localhost:8000/rooms?hotel_id=1"       # all rooms for this hotel
curl "http://localhost:8000/rooms/available?hotel_id=1"   # only bookable rooms
curl http://localhost:8000/rooms/1                  # one specific room
```

**Behind the scenes:** `RoomController` has three read methods backed by `RoomRepository.findByHotelId()` and `findByHotelIdAndIsavailableTrue()` — Spring Data JPA generates the SQL for both just from the method name.

**Acceptance criteria met:** ✅ rooms shown per hotel; the `available` filter only returns bookable rooms.

---

### US-008 — Add Room
**Epic:** Room Management · **Role:** Admin

**Try it:**
```bash
curl -X POST http://localhost:8000/rooms \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -d '{"hotel_id":1,"room_no":"101","room_type":"Deluxe","rate_per_day":2000}'
```

**Behind the scenes:** `RoomServiceImpl.addRoom()` checks `hotelRepository.existsById()` first — if the hotel doesn't exist you get `404`, not a broken foreign key.

**You get back:**
```json
{"room_id": 1, "hotel_id": 1, "room_no": "101", "room_type": "Deluxe", "rate_per_day": 2000.0, "isavailable": true, "photo": null}
```

**Acceptance criteria met:** ✅ room is linked to the hotel and appears in its room list; adding to a non-existent hotel is rejected.

---

### US-009 — Update Room
**Epic:** Room Management · **Role:** Admin

**Try it:**
```bash
curl -X PUT http://localhost:8000/rooms/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -d '{"rate_per_day":2200,"isavailable":true}'
```

**Behind the scenes:** `RoomServiceImpl.updateRoom()` — same partial-update pattern as hotels.

**Acceptance criteria met:** ✅ rate/type/availability change and are reflected immediately.

---

### US-010 — Delete Room
**Epic:** Room Management · **Role:** Admin

```bash
curl -X DELETE http://localhost:8000/rooms/2 -H "Authorization: Bearer <ADMIN_TOKEN>"
```

**Acceptance criteria met:** ✅ room removed from its hotel's listing. (Use a *different* room ID than the one you're about to book below.)

---

### US-011 — Book a Room
**Epic:** Room Booking · **Role:** Customer · **Auth needed:** Customer token

**What it does:** This is the core feature — reserving a room for a date range. The price is calculated automatically; you never send an amount yourself.

**Try it** (using the customer token from US-002, and room `1` at ₹2,000/night):
```bash
curl -X POST http://localhost:8000/bookings \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <CUSTOMER_TOKEN>" \
  -d '{"hotel_id":1,"room_id":1,"booked_from":"2025-08-01","booked_to":"2025-08-05","no_of_adults":2}'
```

**Behind the scenes** (`BookingServiceImpl.addBooking()`), in order:
1. Confirm the hotel exists → `404` if not.
2. Confirm the room exists → `404` if not.
3. Confirm the room is currently available → `400 "Room is not available"` if someone already booked it.
4. Confirm `booked_to` is after `booked_from` → `400 "Invalid booking dates"` if not.
5. Calculate `amount = rate_per_day × number_of_nights` — here, 4 nights × ₹2,000 = **₹8,000**.
6. Flip the room's `isavailable` to `false` so nobody else can book it.
7. Save the booking with `status = "confirmed"`.

**You get back:**
```json
{"booking_id": 1, "hotel_id": 1, "room_id": 1, "user_id": 1, "booked_from": "2025-08-01", "booked_to": "2025-08-05", "no_of_adults": 2, "no_of_children": 0, "amount": 8000.0, "status": "confirmed"}
```

Try booking the same room again right now — you'll get `400 "Room is not available"`, which is exactly what should happen.

**Acceptance criteria met:** ✅ booking created; amount auto-calculated correctly; room becomes unavailable for anyone else.

---

### US-012 — View Booking History
**Epic:** Room Booking · **Role:** Customer

**Try it:**
```bash
curl http://localhost:8000/bookings/my -H "Authorization: Bearer <CUSTOMER_TOKEN>"
```

**Behind the scenes:** `BookingController.getMyBookings()` pulls the user's ID directly out of their own JWT (not from the URL — you can't ask for someone else's bookings) and calls `BookingServiceImpl.getBookingsByUser()`.

**Acceptance criteria met:** ✅ only your own bookings are returned, never anyone else's; empty list if you have none yet.

---

### US-013 — Cancel Booking
**Epic:** Room Booking · **Role:** Customer

**Try it:**
```bash
curl -X PUT http://localhost:8000/bookings/1/cancel -H "Authorization: Bearer <CUSTOMER_TOKEN>"
```

**Behind the scenes:** `BookingServiceImpl.cancelBooking()` sets `status = "cancelled"` (the booking row is **kept**, not deleted — it's a soft cancel) and flips the room's `isavailable` back to `true`. Check `GET /rooms/1` afterward and you'll see it's bookable again.

**Acceptance criteria met:** ✅ status becomes `cancelled`; the room frees up immediately; cancelling something that doesn't exist returns `404`.

---

### US-014 — Make a Payment
**Epic:** Payment Management · **Role:** Customer

> For this example, book a fresh room again first (US-011) since you just cancelled booking `1` above — say the new one comes back as `booking_id: 2`.

**Try it:**
```bash
curl -X POST http://localhost:8000/payments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <CUSTOMER_TOKEN>" \
  -d '{"booking_id":2,"payment_method":"card"}'
```

**Behind the scenes** (`PaymentServiceImpl.addPayment()`):
1. Confirm the booking exists → `404` if not.
2. Confirm there **isn't already a payment** for this booking → `400 "Payment already made for this booking"` if there is (try sending the same request twice to see this).
3. Create a `Transaction` row first (amount copied from the booking, `status = "success"`).
4. Create a `Payment` row linking to that transaction (`status = "completed"`).

**You get back:**
```json
{
  "payment_id": 1, "booking_id": 2, "transaction_id": 1,
  "payment_date": "2026-07-02T10:50:02.12",
  "status": "completed",
  "transaction": {"transaction_id": 1, "amount": 8000.0, "payment_method": "card", "status": "success", "created_at": "2026-07-02T10:50:02.12"}
}
```

**Acceptance criteria met:** ✅ payment + transaction both created; paying for the same booking twice is rejected; paying for a booking that doesn't exist returns `404`.

---

### US-015 — View Payments
**Epic:** Payment Management · **Role:** Admin

```bash
curl http://localhost:8000/payments -H "Authorization: Bearer <ADMIN_TOKEN>"
```

**Behind the scenes:** `PaymentServiceImpl.getAllPayments()` returns every payment with its transaction nested inside — this is exactly what the admin "Total Revenue" card on the frontend sums up.

**Acceptance criteria met:** ✅ every payment visible with its amount; total revenue is just `sum(transaction.amount)`.

---

### US-016 — Manage Customers
**Epic:** Customer Management · **Role:** Admin

```bash
curl http://localhost:8000/users -H "Authorization: Bearer <ADMIN_TOKEN>"
curl -X DELETE http://localhost:8000/users/1 -H "Authorization: Bearer <ADMIN_TOKEN>"
```

**Behind the scenes:** `UserController.getAllUsers()` / `.deleteUser()` → `UserServiceImpl`. A customer token hitting `GET /users` gets `403 "Admin access required"` — this rule lives declaratively in `SecurityConfig`, not as an `if` statement scattered through the controller.

**Acceptance criteria met:** ✅ admin can list and delete customer accounts; a customer cannot.

---

### US-017 — Admin Dashboard
**Epic:** Admin Dashboard · **Role:** Admin

**What it does:** Shows stat cards (total hotels, bookings, users, revenue) and the 5 most recent bookings.

**How it actually works:** there is **no dedicated `/admin/dashboard` backend endpoint** — and that's intentional, not a gap. `AdminDashboardPage.jsx` calls the four endpoints you already have (`GET /hotels`, `GET /bookings`, `GET /users`, `GET /payments`) in parallel and computes the stats in the browser:

```javascript
Promise.all([hotelService.getAll(), bookingService.getAll(), userService.getAll(), paymentService.getAll()])
  .then(([hotels, bookings, users, payments]) => {
    const revenue = payments.data.reduce((sum, p) => sum + (p.transaction?.amount || 0), 0)
    setStats({ hotels: hotels.data.length, bookings: bookings.data.length, users: users.data.length, revenue })
    setRecentBookings(bookings.data.slice(-5).reverse())
  })
```

This keeps the backend simple — every number on the dashboard is just a reuse of an endpoint you've already tested above.

**Acceptance criteria met:** ✅ all four stats and the recent-bookings list are correct, because they're computed directly from the same data you can independently verify via `GET /hotels`, `/bookings`, `/users`, `/payments`.

---

## 6. Data Model Reference

Seven tables, matching seven JPA entity classes in `entity/`:

| Table | Key fields | Relationships |
|---|---|---|
| `users` | `userId`, `userName`, `email` (unique), `password` (hashed), `role`, `mobile`, `address` | 1 user → many bookings |
| `admins` | `adminId`, `adminName`, `email` (unique), `password` (hashed) | — |
| `hotels` | `hotelId`, `city`, `hotelName`, `avgRatePerDay`, contact fields | 1 hotel → many rooms, many bookings |
| `room_details` | `roomId`, `hotelId` (FK), `roomNo`, `roomType`, `ratePerDay`, `isavailable`, `photo` | 1 room → many bookings over time |
| `booking_details` | `bookingId`, `hotelId`/`roomId`/`userId` (FK), `bookedFrom`/`bookedTo`, `amount`, `status` | 1 booking → 1 payment |
| `transactions` | `transactionId`, `amount`, `paymentMethod`, `status`, `createdAt` | 1 transaction → 1 payment |
| `payments` | `paymentId`, `bookingId` (FK), `transaction` (`@OneToOne`), `paymentDate`, `status` | joins booking ↔ transaction |

**Behaviors enforced in service code (not database constraints):**
- Booking a room sets `isavailable = false`; cancelling sets it back to `true`.
- `amount` is always `ratePerDay × nights`, computed server-side — never trust a client-sent amount.
- Deleting a hotel deletes its rooms first (see US-006).
- A booking can have at most one payment (see US-014).

---

## 7. Full API Reference

**Base URL:** `http://localhost:8000` · **Interactive docs:** `http://localhost:8000/swagger-ui/index.html`

| Method | Endpoint | Who | Story |
|---|---|---|---|
| POST | `/auth/register` | Public | US-001 |
| POST | `/auth/login` | Public | US-002 |
| POST | `/auth/admin/register` | Public | — (setup only) |
| POST | `/auth/admin/login` | Public | US-002 |
| GET | `/hotels` `?city=` | Public | US-003 |
| GET | `/hotels/{id}` | Public | US-003 |
| POST | `/hotels` | Admin | US-004 |
| PUT | `/hotels/{id}` | Admin | US-005 |
| DELETE | `/hotels/{id}` | Admin | US-006 |
| GET | `/rooms` `?hotel_id=` | Public | US-007 |
| GET | `/rooms/available?hotel_id=` | Public | US-007 |
| GET | `/rooms/{id}` | Public | US-007 |
| POST | `/rooms` | Admin | US-008 |
| PUT | `/rooms/{id}` | Admin | US-009 |
| DELETE | `/rooms/{id}` | Admin | US-010 |
| POST | `/bookings` | Customer | US-011 |
| GET | `/bookings/my` | Customer | US-012 |
| GET | `/bookings/{id}` | Customer | US-012 |
| PUT | `/bookings/{id}/cancel` | Customer | US-013 |
| GET | `/bookings` | Admin | US-015-adjacent (admin view of all bookings) |
| PUT | `/bookings/{id}/status` | Admin | manual override |
| POST | `/payments` | Customer | US-014 |
| GET | `/payments/{id}` | Customer | US-014 |
| GET | `/payments` | Admin | US-015 |
| GET | `/users` | Admin | US-016 |
| GET | `/users/{id}` | Customer (own profile) | — |
| PUT | `/users/{id}` | Customer (own profile) | — |
| DELETE | `/users/{id}` | Admin | US-016 |

---

## 8. Authentication Flow

```
Register  →  password BCrypt-hashed, saved
Login     →  password checked against hash
          →  JWT issued: { sub: <id>, role: "customer"|"admin", exp: +24h }, signed HS256
          →  frontend stores it in localStorage

Every later request:
  Axios attaches "Authorization: Bearer <token>"
    → JwtAuthFilter runs first, validates the token, records who's calling and their role
      → SecurityConfig's rules check: does this role match what this URL requires?
          allowed    → request reaches the controller
          no token / bad token → 401 {"detail": "Invalid or expired token"}
          wrong role            → 403 {"detail": "Admin access required"}
```

The frontend's Axios interceptor (`services/api.js`) watches for `401` responses and automatically logs the user out and redirects to `/login` — so an expired token never leaves someone stuck on a broken page.

---

## 9. Design Patterns Used

| Pattern | Where | Why it's there |
|---|---|---|
| Layered architecture | whole backend | Controller → Service → Repository → Entity, each layer only knows the one below it |
| Repository pattern | `repository/` | All database access goes through Spring Data JPA interfaces — no raw SQL anywhere else |
| Service layer | `service/` | Business rules (availability, duplicate checks, amount math) live in exactly one place |
| DTO pattern | `dto/` | The JSON shape the API speaks is decoupled from the database entity shape |
| Dependency injection | constructor injection everywhere | Spring wires services and repositories together — nothing is manually instantiated |
| Global exception handler | `GlobalExceptionHandler` | One place converts every exception into a consistent `{"detail": "..."}` response |
| Context API (frontend) | `AuthContext.jsx` | Login state is available to every page without prop-drilling |
| Interceptor (frontend) | `api.js` | The JWT is attached to every request automatically, and 401s are handled globally |

---

## 10. Troubleshooting

| Symptom | Cause | Fix |
|---|---|---|
| Nothing happens when starting the backend | Ran the command from the wrong folder | `cd backend` first |
| `Database may be already in use` | A previous run is still holding the H2 file lock | Find and stop the other `java` process running `HotelbookingApplication`, then retry |
| `422 Unprocessable Entity` | A required field is missing from the request body | Check the field against the Swagger schema for that endpoint |
| `401 Unauthorized` | Token missing, malformed, or expired | Log in again |
| `403 Forbidden` | Right token, wrong role for this endpoint | Use an admin token for admin-only endpoints |
| `400 "Room is not available"` | That room is already booked | Pick a different room, or cancel the existing booking first |
| `400 "Payment already made for this booking"` | You're paying for the same booking twice | Check `/bookings/my` — it's already paid |
| CORS error in the browser console | Frontend origin isn't in the allowed list | Confirm you're on `localhost:5173` — that's the only origin `SecurityConfig` allows by default |
| Old data won't go away after changing an entity | H2's auto-schema-update got confused | Stop the app, delete `backend/hotel_booking.mv.db`, restart (tables are recreated empty) |

---

## 11. Testing Checklist (Manual QA)

Use this to sanity-check the whole app in one pass — either clicking through the **frontend** (`http://localhost:5173`) or hitting the **API directly** (Swagger at `http://localhost:8000/swagger-ui/index.html`, or the `curl` commands in Section 5). Do it in order — later steps depend on data created by earlier ones.

### Setup
- [ ] Backend started (`cd backend`, then `.\mvnw.cmd spring-boot:run` in PowerShell) — see `Started HotelbookingApplication` in the console
- [ ] Frontend started (`cd frontend && npm run dev`) — `http://localhost:5173` loads
- [ ] `POST /auth/admin/register` — first admin account created

### Login Management
- [ ] **US-001** Register a new customer → success message with a `user_id`
- [ ] **US-001** Register again with the *same* email → rejected with `400`
- [ ] **US-002** Log in as that customer → get back a token + `role: "customer"`
- [ ] **US-002** Log in as admin → get back a token + `role: "admin"`
- [ ] **US-002** Log in with a wrong password → `401`

### Hotel Management
- [ ] **US-003** `GET /hotels` with no hotels yet → empty list, not an error
- [ ] **US-004** As admin, create a hotel → appears in `GET /hotels`
- [ ] **US-004** As customer, try to create a hotel → `403`
- [ ] **US-003** `GET /hotels?city=<city>` → only matching hotels come back, case doesn't matter
- [ ] **US-005** Update the hotel's rate → change reflected on `GET /hotels/{id}`
- [ ] **US-006** Delete a *throwaway* test hotel → gone from the list, and its rooms are gone too

### Room Management
- [ ] **US-007** `GET /rooms?hotel_id={id}` → rooms for that hotel only
- [ ] **US-008** As admin, add a room to the hotel → appears in the room list
- [ ] **US-008** Try adding a room to a hotel ID that doesn't exist → `404`
- [ ] **US-009** Update the room's rate/type → reflected immediately
- [ ] **US-007** `GET /rooms/available?hotel_id={id}` → only rooms with `isavailable: true`
- [ ] **US-010** Delete a *different, unused* room → gone from the list

### Room Booking
- [ ] **US-011** As customer, book an available room → booking returned with `status: "confirmed"` and a correctly calculated `amount`
- [ ] **US-011** Check that room again (`GET /rooms/{id}`) → `isavailable` is now `false`
- [ ] **US-011** Try booking the *same* room again → `400 "Room is not available"`
- [ ] **US-011** Try booking with `booked_to` before `booked_from` → `400 "Invalid booking dates"`
- [ ] **US-012** `GET /bookings/my` → your booking shows up
- [ ] **US-012** Log in as a *different* customer and check `/bookings/my` → their list does **not** include the first customer's booking
- [ ] **US-013** Cancel the booking → `status: "cancelled"`
- [ ] **US-013** Check the room again → `isavailable` is back to `true`

### Payment Management
- [ ] **US-011** Book a fresh room again (since the last one was cancelled)
- [ ] **US-014** Pay for that booking → payment returned with a nested `transaction` object
- [ ] **US-014** Pay for the *same* booking again → `400 "Payment already made for this booking"`
- [ ] **US-014** Pay for a booking ID that doesn't exist → `404`
- [ ] **US-015** As admin, `GET /payments` → the payment appears with its transaction amount

### Customer Management
- [ ] **US-016** As admin, `GET /users` → the registered customer appears
- [ ] **US-016** As customer, `GET /users` → `403`
- [ ] **US-016** As admin, delete a *throwaway* test customer → gone from the list

### Admin Dashboard
- [ ] **US-017** Log in as admin on the frontend, open `/admin/dashboard` → hotel/booking/user counts and revenue match what you'd count by hand from the lists above
- [ ] **US-017** "Recent Bookings" on the dashboard shows your latest booking

### Cross-cutting checks
- [ ] Hit any protected endpoint with **no** token → `401 "Invalid or expired token"`
- [ ] Hit any admin-only endpoint with a **customer** token → `403 "Admin access required"`
- [ ] Refresh the frontend after logging in → still logged in (token persisted in `localStorage`)
- [ ] Log out on the frontend, then try to visit `/my-bookings` directly → redirected to `/login`

If every box above is checked, all 17 user stories are confirmed working end to end.

---

*Navikenz Internship Capstone — Hotel Booking Management System*
*Spring Boot 3.5 · Java 17 · React 18 · Spring Data JPA · JWT · H2*
