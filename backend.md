# Backend — What's What and Why

The backend is a **Java + Spring Boot REST API**. It owns the database and every business rule (can this room be booked? is this user an admin?). The frontend never touches the database directly — it only ever talks to this API over HTTP.

**Stack:** Java 17 · Spring Boot 3.5 · Spring Data JPA · Spring Security · H2 (file-based database) · JWT (`io.jsonwebtoken`) · springdoc-openapi (Swagger UI).

The code is written in **plain, explicit Java on purpose** — every class has a real constructor and real getters/setters you can read, instead of annotations (like Lombok) generating that code invisibly. If you can see a method, it exists in the source; nothing is generated behind the scenes.

---

## Folder structure

```
backend/src/main/java/com/navikenz/hotelbooking/
├── HotelbookingApplication.java     The main() method — starts everything
├── entity/          7 classes — one per database table
├── repository/      7 interfaces — Spring Data JPA, zero hand-written SQL
├── dto/             Request/response shapes, grouped by feature
├── service/         Business rules — one interface + one *Impl per feature
├── controller/      The actual REST endpoints (the HTTP "front door")
├── security/        JwtService (sign/verify tokens), JwtAuthFilter (checks every request)
├── config/          SecurityConfig (who can call what), OpenApiConfig (Swagger)
└── exception/       Turns thrown errors into clean {"detail": "..."} JSON responses
```

---

## The request flow (the most important thing to understand)

Every single feature — hotels, rooms, bookings, whatever — is built the same four-layer way, and a request always flows in **one direction only**:

```
Controller  →  Service  →  Repository  →  Database
   ↑                                          │
   └──────────── DTO response ────────────────┘
```

1. **Controller** reads the incoming HTTP request (URL, JSON body) and immediately hands it to a Service. It contains *no* business logic — just "receive this, call that, return the result."
2. **Service** (the `*Impl` classes) is where all the actual rules live: is the room available? is the date range valid? what's the price? This is the only layer that makes decisions.
3. **Repository** is a plain interface — you don't write SQL. Spring Data JPA reads the method's *name* and generates the query for you (see below).
4. Whatever comes back gets converted into a **DTO** (a plain response object) before being handed back to the controller, and out to the browser as JSON.

Nothing skips a layer. A controller never talks to a repository directly, and a repository never knows anything about HTTP.

---

## `entity/` — the 7 database tables

Each class here is a `@Entity` — Spring Data JPA turns it into a real SQL table automatically (you never write `CREATE TABLE` yourself).

| Class | Table | Key fields |
|---|---|---|
| `User` | `users` | `userId`, `userName`, `email` (unique), `password` (hashed), `role` (`"customer"`) |
| `Admin` | `admins` | `adminId`, `adminName`, `email` (unique), `password` (hashed) |
| `Hotel` | `hotels` | `hotelId`, `city`, `hotelName`, `avgRatePerDay`, contact fields |
| `RoomDetails` | `room_details` | `roomId`, `hotelId` (FK), `roomNo`, `ratePerDay`, `isavailable` |
| `BookingDetails` | `booking_details` | `bookingId`, `hotelId`/`roomId`/`userId` (FK), `bookedFrom`/`bookedTo`, `amount`, `status` |
| `Transaction` | `transactions` | `transactionId`, `amount`, `paymentMethod`, `status`, `createdAt` |
| `Payment` | `payments` | `paymentId`, `bookingId` (FK), `transaction` (`@OneToOne`), `paymentDate` |

Every entity is just: `@Column`-annotated private fields + a no-argument constructor (required by JPA) + plain `getX()`/`setX()` methods. No shortcuts, no generated code — what you see is what runs.

**Rules enforced in code, not by the database itself:**
- Booking a room sets `isavailable = false`; cancelling it sets it back to `true`.
- `amount` is always calculated on the server as `ratePerDay × nights` — the client can never send its own price.
- Deleting a hotel deletes its rooms first, so nothing is ever left pointing at a hotel that no longer exists.
- A booking can have at most one payment.

---

## `repository/` — how database queries get written without SQL

Example, from `RoomRepository`:

```java
public interface RoomRepository extends JpaRepository<RoomDetails, Long> {
    List<RoomDetails> findByHotelId(Long hotelId);
    List<RoomDetails> findByHotelIdAndIsavailableTrue(Long hotelId);
    boolean existsByHotelIdAndRoomNoIgnoreCase(Long hotelId, String roomNo);
}
```

Spring Data JPA parses the *method name itself* and builds the SQL: `findByHotelId` becomes `SELECT * FROM room_details WHERE hotel_id = ?`, `findByHotelIdAndIsavailableTrue` adds `AND isavailable = true`, and so on. You only ever declare the method signature — never the query.

---

## `dto/` — the shape of what goes over HTTP

DTO = Data Transfer Object. These are plain classes whose *only* job is to define exactly what JSON a request/response looks like — kept deliberately separate from the `entity/` classes (which define the *database* shape). This separation means, for example, a `User` entity has a `password` field, but `UserResponse` (what actually gets sent to the browser) does not — so a password hash can never accidentally leak out over the API.

Each feature has up to three DTOs:
- **`...CreateRequest`** — what the client sends to create something (e.g. `HotelCreateRequest`)
- **`...UpdateRequest`** — what the client sends to partially update something (all fields optional — `null` means "don't change this field")
- **`...Response`** — what the client receives back

---

## `service/` and `service/impl/` — where the actual rules live

One interface (`HotelService`) + one implementation class (`HotelServiceImpl`) per feature. This is the layer worth reading closely if you want to understand *behavior*, not just data shape. For example, `BookingServiceImpl.addBooking()`:

1. Confirm the hotel exists → `404` if not.
2. Confirm the room exists → `404` if not.
3. Confirm the room is currently available → `400 "Room is not available"` if not.
4. Confirm `bookedTo` is after `bookedFrom` → `400 "Invalid booking dates"` if not.
5. Calculate `amount = ratePerDay × nights`.
6. Flip the room to unavailable so nobody else can book it.
7. Save the booking with `status = "confirmed"`.

Every service class follows the same internal pattern: a constructor takes in the repositories it needs (via `@Autowired`), public methods implement one rule each, and a small private `toResponse(...)` helper converts an entity into its matching DTO at the end.

---

## `controller/` — the actual REST endpoints

Each controller is a thin `@RestController` mapped to a URL prefix (`@RequestMapping("/hotels")`, etc.). It reads path variables/query params/request bodies, calls exactly one service method, and returns whatever the service gives back — Spring automatically turns the returned object into JSON.

A few controllers (`BookingController`, `PaymentController`, `UserController`) also do one extra check: **"is the logged-in user either an admin, or the actual owner of this record?"** — e.g. a customer can fetch their *own* booking by ID, but not someone else's. This check reads the caller's role straight out of their JWT (via Spring's `Authentication` object), not from anything the client claims in the request body.

| Endpoint | Method | Who | Feature |
|---|---|---|---|
| `/auth/register`, `/auth/login` | POST | Public | Customer sign-up / login |
| `/auth/admin/register`, `/auth/admin/login` | POST | Public | Admin sign-up / login |
| `/hotels` | GET | Public | List/search hotels |
| `/hotels`, `/hotels/{id}` | POST / PUT / DELETE | Admin | Manage hotels |
| `/rooms`, `/rooms/available` | GET | Public | List rooms |
| `/rooms`, `/rooms/{id}` | POST / PUT / DELETE | Admin | Manage rooms |
| `/bookings` | POST | Customer | Book a room |
| `/bookings/my` | GET | Customer | Your own bookings |
| `/bookings/{id}/cancel` | PUT | Owner or Admin | Cancel a booking |
| `/bookings` | GET | Admin | All bookings |
| `/payments` | POST | Customer | Pay for a booking |
| `/payments` | GET | Admin | All payments |
| `/users` | GET / DELETE | Admin | Manage customers |
| `/users/{id}` | GET / PUT | Owner or Admin | View/update own profile |

---

## `security/` and `config/SecurityConfig.java` — how login actually works

1. **Register** — password is hashed with `BCryptPasswordEncoder` before it's saved. It is never stored, logged, or returned as plain text.
2. **Login** — the submitted password is checked against the stored hash with `passwordEncoder.matches(...)`. If it matches, `JwtService.generateToken()` creates a signed JWT containing the user's ID and role (`customer` or `admin`), valid for 24 hours.
3. **Every later request** carries that token as `Authorization: Bearer <token>`. `JwtAuthFilter` runs on *every* incoming request, before it reaches any controller: it decodes the token and tells Spring Security "this request is coming from user X with role Y."
4. **`SecurityConfig`** is a single file that declares, per URL and HTTP method, who's allowed in — e.g. `POST /hotels` requires `hasRole("ADMIN")`, `GET /hotels` is `permitAll()`. This is enforced by the framework itself before your controller code even runs — a customer token physically cannot reach an admin-only endpoint, it's not just hidden in the UI.

If a token is missing/invalid → `401 "Invalid or expired token"`. If the token is valid but the wrong role → `403 "Admin access required"`.

---

## `exception/GlobalExceptionHandler.java` — one place for every error

Instead of every controller writing its own try/catch, services just `throw` a meaningful exception (`ResourceNotFoundException`, `BadRequestException`, etc.), and this one class catches each type and turns it into a consistent JSON shape:

```json
{ "detail": "Room is not available" }
```

with the matching HTTP status code (404, 400, 401, 403, 422). This is why every error from the API looks the same regardless of which feature it came from.

---

## `config/OpenApiConfig.java` — Swagger UI

Wires up **springdoc-openapi**, which auto-generates the interactive API docs at `http://localhost:8000/swagger-ui/index.html` directly from your `@RestController` classes and DTOs — no separate documentation to maintain by hand.

---

## Why the code looks the way it does

Every class in this backend uses only core Java + core Spring features you'd learn in an intro course: plain constructors, plain getters/setters, `for` loops instead of streams, `new X(); x.setY(...);` instead of a builder pattern, and no Lombok. Nothing is generated by an annotation you can't see the source of — if a method exists on an object, you can open the file and read it.
