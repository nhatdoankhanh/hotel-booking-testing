# Hotel Booking Management System
### Navikenz Internship — Capstone Project

**Stack:** Python (FastAPI) + JavaScript (React)  
**Database:** SQLite (auto-created, no setup needed)  
**Auth:** JWT tokens (24-hour expiry)

---

## Table of Contents

1. [Quick Start — How to Run in 5 Minutes](#1-quick-start--how-to-run-in-5-minutes)
2. [What Every File Does](#2-what-every-file-does)
3. [Dependencies — What They Are & Why We Use Them](#3-dependencies--what-they-are--why-we-use-them)
4. [Problem Statement & Modules](#4-problem-statement--modules)
5. [User Stories (All 17)](#5-user-stories-all-17)
6. [System Architecture](#6-system-architecture)
7. [Data Models (Class Design)](#7-data-models-class-design)
8. [Database Tables & Relationships](#8-database-tables--relationships)
9. [Service Interfaces](#9-service-interfaces)
10. [Repository Layer](#10-repository-layer)
11. [All API Endpoints](#11-all-api-endpoints)
12. [Frontend Pages & Routes](#12-frontend-pages--routes)
13. [How Auth Works (JWT Flow)](#13-how-auth-works-jwt-flow)
14. [Sprint Roadmap](#14-sprint-roadmap)
15. [TDD — Test Cases per User Story](#15-tdd--test-cases-per-user-story)
16. [Design Patterns Used](#16-design-patterns-used)
17. [Common Errors & Fixes](#17-common-errors--fixes)

---

## 1. Quick Start — How to Run in 5 Minutes

> **Requirements:** Python 3.10+, Node.js 18+, a terminal

### Step 1 — Start the Backend

Open a terminal and run these commands **one by one**:

```bash
cd e:\NAVIKENZ_INTERN\Hotel_Booking_system\backend

# Create a virtual environment (only first time)
python -m venv venv

# Activate it (Windows)
venv\Scripts\activate

# Install all packages (only first time)
pip install -r requirements.txt

# Start the server
uvicorn app.main:app --reload --port 8000
```

You should see: `Uvicorn running on http://127.0.0.1:8000`

**Backend URLs:**
| URL | Purpose |
|---|---|
| `http://localhost:8000` | API root (health check) |
| `http://localhost:8000/docs` | Swagger UI — test all APIs here |
| `http://localhost:8000/redoc` | ReDoc API reference |

---

### Step 2 — Create Your First Admin Account

Open `http://localhost:8000/docs`, find `POST /auth/admin/register`, click **Try it out**, and send:

```json
{
  "admin_name": "Admin",
  "email": "admin@hotel.com",
  "password": "admin123"
}
```

> You only need to do this once. The admin data is saved in `hotel_booking.db`.

---

### Step 3 — Start the Frontend

Open a **second terminal**:

```bash
cd e:\NAVIKENZ_INTERN\Hotel_Booking_system\frontend

# Install packages (only first time)
npm install

# Start the app
npm run dev
```

Open `http://localhost:5173` in your browser.

---

### Step 4 — Test the Full Flow

| Who | What to do |
|---|---|
| **Admin** | Login at `/login` → Admin tab → Go to Hotels → Add a hotel → Go to Rooms → Add rooms to that hotel |
| **Customer** | Register at `/register` → Login → Browse Hotels → Click a hotel → Click "Book Now" on a room → Fill dates → Proceed to Payment |

---

## 2. What Every File Does

### Backend Files (`backend/`)

```
backend/
├── .env                          ← Secret key config (don't share this)
├── requirements.txt              ← All Python packages needed
│
└── app/
    ├── main.py                   ← Entry point. Starts FastAPI, adds CORS, registers all routers
    ├── database.py               ← Creates SQLite connection and DB session factory
    │
    ├── core/
    │   ├── config.py             ← Reads .env file (SECRET_KEY, token expiry time)
    │   ├── security.py           ← bcrypt password hashing + JWT token create/decode
    │   └── deps.py               ← Auth guards: require_admin(), require_customer()
    │
    ├── models/                   ← SQLAlchemy ORM — each file = one database table
    │   ├── user.py               ← TABLE: users
    │   ├── admin.py              ← TABLE: admins
    │   ├── hotel.py              ← TABLE: hotels
    │   ├── room_details.py       ← TABLE: room_details
    │   ├── booking_details.py    ← TABLE: booking_details
    │   ├── transaction.py        ← TABLE: transactions
    │   └── payment.py            ← TABLE: payments
    │
    ├── schemas/                  ← Pydantic models — validates input/output shape
    │   ├── user.py               ← UserCreate, UserUpdate, UserResponse, Token
    │   ├── admin.py              ← AdminCreate, AdminLogin, AdminResponse
    │   ├── hotel.py              ← HotelCreate, HotelUpdate, HotelResponse
    │   ├── room_details.py       ← RoomDetailsCreate, RoomDetailsUpdate, RoomDetailsResponse
    │   ├── booking_details.py    ← BookingDetailsCreate, BookingDetailsResponse
    │   └── payment.py            ← PaymentCreate, PaymentResponse, PaymentDetailResponse
    │
    ├── repositories/             ← Data access layer — raw DB queries live here
    │   ├── user_repository.py    ← add/update/remove/showAll/showById/showByEmail
    │   ├── admin_repository.py   ← create/signIn/getByEmail
    │   ├── hotel_repository.py   ← add/update/remove/showAll/show/search
    │   ├── room_repository.py    ← add/update/remove/showAll/show/byHotel/available
    │   ├── booking_repository.py ← add/update/remove(cancel)/showAll/show/byUser
    │   └── payment_repository.py ← add(with transaction)/showAll/show/byBooking
    │
    ├── services/                 ← Business logic layer — rules + validation
    │   ├── user_service.py       ← duplicate email check, password auth, token issue
    │   ├── admin_service.py      ← admin creation, login, sign-out
    │   ├── hotel_service.py      ← hotel exists check, delegates to repo
    │   ├── room_service.py       ← hotel exists check before adding room
    │   ├── booking_service.py    ← availability check, date validation, auto-calc amount
    │   └── payment_service.py    ← booking exists check, duplicate payment check
    │
    └── routers/                  ← HTTP endpoints — what URL maps to what action
        ├── auth.py               ← POST /auth/register, /auth/login, /auth/admin/...
        ├── users.py              ← GET/PUT/DELETE /users and /users/{id}
        ├── hotels.py             ← GET/POST/PUT/DELETE /hotels and /hotels/{id}
        ├── rooms.py              ← GET/POST/PUT/DELETE /rooms and /rooms/{id}
        ├── bookings.py           ← GET/POST /bookings, /bookings/my, cancel
        └── payments.py           ← GET/POST /payments and /payments/{id}
```

---

### Frontend Files (`frontend/`)

```
frontend/
├── package.json                  ← All npm packages + scripts (npm run dev / build)
├── vite.config.js                ← Vite config: React plugin, proxy to backend port 8000
│
├── public/
│   └── index.html                ← HTML shell. React mounts into <div id="root">
│
└── src/
    ├── main.jsx                  ← React entry point. Renders <App /> into root
    ├── App.jsx                   ← All routes defined here. Wraps everything in AuthProvider
    ├── index.css                 ← All global CSS: buttons, cards, tables, forms, modals
    │
    ├── context/
    │   └── AuthContext.jsx       ← Global auth state. Saves token to localStorage.
    │                               useAuth() hook used by all pages
    │
    ├── services/                 ← Axios API call wrappers (no fetch/axios used directly in pages)
    │   ├── api.js                ← Axios instance. Auto-attaches JWT. Redirects on 401
    │   ├── authService.js        ← register(), login(), adminLogin()
    │   ├── hotelService.js       ← getAll(), getById(), create(), update(), delete()
    │   ├── roomService.js        ← getAll(), getAvailable(), getById(), create(), update(), delete()
    │   ├── bookingService.js     ← getAll(), getMy(), getById(), create(), cancel()
    │   ├── paymentService.js     ← getAll(), getById(), makePayment()
    │   └── userService.js        ← getAll(), getById(), update(), delete()
    │
    ├── components/
    │   └── common/
    │       ├── Navbar.jsx         ← Top navigation. Shows different links for guest/customer/admin
    │       └── ProtectedRoute.jsx ← Wrapper that redirects to /login if not authenticated
    │                                PublicOnlyRoute: redirects away if already logged in
    │
    └── pages/                    ← One file per page/screen
        │
        ├── -- CUSTOMER PAGES --
        ├── LoginPage.jsx          ← Login form with Customer/Admin toggle
        ├── RegisterPage.jsx       ← Customer registration form
        ├── HotelsPage.jsx         ← Grid of all hotels + city search bar
        ├── HotelDetailPage.jsx    ← Hotel info + all its rooms + "Book Now" buttons
        ├── BookingPage.jsx        ← Date picker, guest count, amount preview
        ├── PaymentPage.jsx        ← Payment method selector, confirm & pay
        ├── BookingHistoryPage.jsx ← List of user's bookings + cancel button
        │
        └── -- ADMIN PAGES --
        ├── AdminDashboardPage.jsx ← Stats cards (hotels, bookings, users, revenue) + recent bookings
        ├── AdminHotelsPage.jsx    ← Hotels table + Add/Edit/Delete modal
        ├── AdminRoomsPage.jsx     ← Rooms table with hotel filter + Add/Edit/Delete modal
        ├── AdminBookingsPage.jsx  ← All bookings table with status filter
        ├── AdminUsersPage.jsx     ← All users table + delete
        └── AdminPaymentsPage.jsx  ← All payments table + total revenue card
```

---

## 3. Dependencies — What They Are & Why We Use Them

### Backend — `requirements.txt`

| Package | Version | What it does | Why we need it |
|---|---|---|---|
| `fastapi` | 0.111.0 | The web framework | Creates REST APIs with Python. Auto-generates Swagger docs |
| `uvicorn[standard]` | 0.29.0 | ASGI server | Runs the FastAPI app. `--reload` restarts on code change |
| `sqlalchemy` | 2.0.30 | ORM (Object Relational Mapper) | Lets us write Python classes instead of raw SQL queries |
| `pydantic[email]` | 2.7.1 | Data validation library | Validates request body shape. Throws errors on wrong input |
| `pydantic-settings` | 2.2.1 | Settings from `.env` file | Reads SECRET_KEY and other config from the `.env` file |
| `python-jose[cryptography]` | 3.3.0 | JWT library | Creates and decodes JWT tokens for authentication |
| `passlib[bcrypt]` | 1.7.4 | Password hashing | Securely hashes passwords. `bcrypt` is industry-standard |
| `python-multipart` | 0.0.9 | Form data parsing | Required by FastAPI for OAuth2 form login support |

**How to install:**
```bash
pip install -r requirements.txt
```

**How to add a new package:**
```bash
pip install some-package
pip freeze > requirements.txt   # update the file
```

---

### Frontend — `package.json`

| Package | Version | What it does | Why we need it |
|---|---|---|---|
| `react` | 18.3.1 | UI library | Core React — component rendering, hooks (useState, useEffect) |
| `react-dom` | 18.3.1 | DOM renderer | Mounts React into the HTML page |
| `react-router-dom` | 6.23.1 | Client-side routing | Lets us navigate between pages without full page reload |
| `axios` | 1.6.8 | HTTP client | Makes API calls to backend. Handles interceptors (JWT attachment) |
| `vite` | 5.2.11 | Build tool (dev dependency) | Fast dev server + production build bundler |
| `@vitejs/plugin-react` | 4.3.0 | Vite plugin (dev dependency) | Enables JSX and React Fast Refresh in Vite |

**How to install:**
```bash
npm install
```

**How to add a new package:**
```bash
npm install package-name
```

---

## 4. Problem Statement & Modules

**Goal:** Build a Hotel Booking Management System where:

- **Customers** can: view hotels, view rooms, book rooms, view/cancel their bookings, make payments
- **Admins** can: manage hotels (CRUD), manage rooms (CRUD), view all bookings, view payments/revenue, manage customers

### 6 Modules

| # | Module | Who uses it | What it covers |
|---|---|---|---|
| 1 | **Login Module** | Both | Register, login with JWT, role-based access |
| 2 | **Customer Module** | Admin | View and delete customer accounts |
| 3 | **Hotel Management Module** | Admin | Add, update, delete, list hotels |
| 4 | **Rooms Management Module** | Admin + Customer | Admin manages rooms; customers view them |
| 5 | **Rooms Booking Module** | Customer | Book room, view history, cancel bookings |
| 6 | **Payments Module** | Admin + Customer | Customer pays; admin views all revenue |

---

## 5. User Stories (All 17)

| ID | Epic | Story | Done by | Acceptance Criteria |
|---|---|---|---|---|
| US-001 | Login Management | User Registration | Customer | Account created, email saved |
| US-002 | Login Management | User Login | Customer/Admin | JWT token returned on success |
| US-003 | Hotel Management | View Hotels | Customer | All hotels listed; search by city works |
| US-004 | Hotel Management | Add Hotel | Admin | Hotel appears in list after creation |
| US-005 | Hotel Management | Update Hotel | Admin | Hotel details updated in DB |
| US-006 | Hotel Management | Delete Hotel | Admin | Hotel + its rooms removed |
| US-007 | Room Management | View Rooms | Customer | Rooms shown per hotel; availability shown |
| US-008 | Room Management | Add Room | Admin | Room linked to hotel, appears in list |
| US-009 | Room Management | Update Room | Admin | Rate/type/availability updated |
| US-010 | Room Management | Delete Room | Admin | Room removed from hotel |
| US-011 | Room Booking | Book Room | Customer | Booking created, room marked unavailable |
| US-012 | Room Booking | View Booking History | Customer | User sees only their own bookings |
| US-013 | Room Booking | Cancel Booking | Customer | Status = cancelled, room becomes available |
| US-014 | Payment Management | Make Payment | Customer | Payment + transaction record created |
| US-015 | Payment Management | View Payments | Admin | All payments with amounts visible |
| US-016 | Customer Management | Manage Customers | Admin | View/delete customer accounts |
| US-017 | Admin Dashboard | Admin Dashboard | Admin | Stats: hotels, bookings, users, revenue |

---

## 6. System Architecture

```
  BROWSER (React App — localhost:5173)
       │
       │  HTTP REST calls with "Authorization: Bearer <token>"
       │  (via Axios — src/services/api.js)
       ▼
  FASTAPI BACKEND (localhost:8000)
  ┌─────────────────────────────────────────────┐
  │  ROUTER LAYER  (app/routers/)               │
  │  /auth  /hotels  /rooms  /bookings          │
  │  /payments  /users                          │
  │       │ validates input with Pydantic       │
  │       │ checks JWT via deps.py              │
  ├───────▼─────────────────────────────────────┤
  │  SERVICE LAYER  (app/services/)             │
  │  Business rules, amount calculation,        │
  │  availability checks, duplicate checks      │
  ├───────▼─────────────────────────────────────┤
  │  REPOSITORY LAYER  (app/repositories/)      │
  │  All SQLAlchemy queries. One class per       │
  │  entity. Returns ORM model objects.         │
  ├───────▼─────────────────────────────────────┤
  │  MODEL LAYER  (app/models/)                 │
  │  SQLAlchemy ORM — maps Python class to DB   │
  └───────▼─────────────────────────────────────┘
       │
       ▼
  SQLite DATABASE  (backend/hotel_booking.db)
  Tables: users, admins, hotels, room_details,
          booking_details, transactions, payments
```

### Data flow for a booking request

```
Customer clicks "Confirm Booking"
  → BookingPage.jsx calls bookingService.create(data)
    → api.js attaches JWT token to request headers
      → POST /bookings hits bookings.py router
        → require_customer() checks JWT is valid
          → booking_service.add_booking_details() runs:
              1. Check hotel exists
              2. Check room exists
              3. Check room is available
              4. Check dates are valid (checkout > checkin)
              5. Calculate amount = rate × nights
              6. Mark room as unavailable
              7. Insert booking into DB
          → Return booking JSON to frontend
            → Navigate to PaymentPage
```

---

## 7. Data Models (Class Design)

These are the Python classes in `app/models/`. Each class = one DB table.

### User (`models/user.py`)
```
Field         Type           Notes
─────────────────────────────────────────────────
user_id       Integer        Primary key, auto-increments
user_name     String(100)    Full name
email         String(150)    Unique — used for login
password      String(255)    Stored as bcrypt hash, NEVER plain text
role          String(20)     "customer" (default) or "admin"
mobile        String(20)     Optional
address       String(300)    Optional
```

### Admin (`models/admin.py`)
```
Field         Type           Notes
─────────────────────────────────────────────────
admin_id      Integer        Primary key
admin_name    String(100)    Display name
email         String(150)    Unique — used for admin login
password      String(255)    bcrypt hashed
```

### Hotel (`models/hotel.py`)
```
Field             Type           Notes
─────────────────────────────────────────────────
hotel_id          Integer        Primary key
city              String(100)    Used for search
hotel_name        String(200)    Required
address           String(400)    Optional
description       String(1000)   Optional
avg_rate_per_day  Float          Shown on listing page
email             String(150)    Contact email
phone1            String(20)     Primary phone
phone2            String(20)     Secondary phone (optional)
website           String(200)    Optional
```

### RoomDetails (`models/room_details.py`)
```
Field         Type           Notes
─────────────────────────────────────────────────
room_id       Integer        Primary key
hotel_id      Integer        Foreign key → hotels.hotel_id
room_no       String(20)     e.g. "101", "A-201"
room_type     String(50)     Standard / Deluxe / Suite / etc.
rate_per_day  Float          Used to calculate booking amount
isavailable   Boolean        True = can be booked. Auto-set to False on booking
photo         String(500)    Image URL (optional)
```

### BookingDetails (`models/booking_details.py`)
```
Field           Type           Notes
─────────────────────────────────────────────────
booking_id      Integer        Primary key
hotel_id        Integer        FK → hotels.hotel_id
room_id         Integer        FK → room_details.room_id
user_id         Integer        FK → users.user_id
booked_from     Date           Check-in date
booked_to       Date           Check-out date
no_of_adults    Integer        Default 1
no_of_children  Integer        Default 0
amount          Float          Auto-calculated: rate_per_day × nights
status          String(20)     confirmed / cancelled / completed
```

### Transaction (`models/transaction.py`)
```
Field           Type           Notes
─────────────────────────────────────────────────
transaction_id  Integer        Primary key
amount          Float          Amount paid
payment_method  String(50)     online / card / upi / cash
status          String(20)     success / failed
created_at      DateTime       Auto-set to current time
```

### Payment (`models/payment.py`)
```
Field           Type           Notes
─────────────────────────────────────────────────
payment_id      Integer        Primary key
booking_id      Integer        FK → booking_details.booking_id (1:1)
transaction_id  Integer        FK → transactions.transaction_id (1:1)
payment_date    DateTime       Auto-set to current time
status          String(20)     completed / pending
```

---

## 8. Database Tables & Relationships

```
users
 └──< booking_details  (one user → many bookings)

hotels
 └──< room_details     (one hotel → many rooms)
 └──< booking_details  (one hotel → many bookings)

room_details
 └──< booking_details  (one room → many bookings over time)

booking_details
 └──  payment          (one booking → one payment, 1:1)

payment
 └──  transaction      (one payment → one transaction, 1:1)
```

### Relationship table

| Table A | Table B | Type | Foreign Key |
|---|---|---|---|
| users | booking_details | 1 : Many | booking_details.user_id |
| hotels | room_details | 1 : Many | room_details.hotel_id |
| hotels | booking_details | 1 : Many | booking_details.hotel_id |
| room_details | booking_details | 1 : Many | booking_details.room_id |
| booking_details | payments | 1 : 1 | payments.booking_id |
| payments | transactions | 1 : 1 | payments.transaction_id |

### Important auto-behaviors (set in code, not DB)

| Trigger | What happens automatically |
|---|---|
| Booking created | `room_details.isavailable` set to `False` |
| Booking cancelled | `room_details.isavailable` set back to `True` |
| Booking created | `amount` = `rate_per_day × number_of_nights` |
| Payment made | A `Transaction` row is also created |

---

## 9. Service Interfaces

Each service file in `app/services/` has a class with these methods:

### UserService (`services/user_service.py`)
```
add_user(db, UserCreate)          → checks no duplicate email → hashes password → saves
update_user(db, user_id, data)    → updates name/mobile/address
remove_user(db, user_id)          → deletes user
show_all_users(db)                → returns all users (admin only)
show_user(db, user_id)            → returns one user
authenticate(db, email, password) → verifies bcrypt hash → returns user or None
login(db, email, password)        → calls authenticate → generates JWT → returns token dict
```

### HotelService (`services/hotel_service.py`)
```
add_hotel(db, HotelCreate)             → creates hotel
update_hotel(db, hotel_id, HotelUpdate) → updates hotel, 404 if not found
remove_hotel(db, hotel_id)             → deletes hotel (rooms cascade-deleted)
show_all_hotels(db)                    → returns all hotels
show_hotel(db, hotel_id)               → returns one hotel, 404 if not found
search_hotels(db, city)                → ILIKE search on city field
```

### RoomService (`services/room_service.py`)
```
add_room_details(db, RoomDetailsCreate)           → checks hotel exists first → creates room
update_room_details(db, room_id, data)            → updates room fields
remove_room_details(db, room_id)                  → deletes room
show_all_roomdetails(db)                          → all rooms
show_roomdetails(db, room_id)                     → one room
show_rooms_by_hotel(db, hotel_id)                 → rooms for a specific hotel
show_available_rooms(db, hotel_id)                → only available rooms for a hotel
```

### BookingService (`services/booking_service.py`)
```
add_booking_details(db, BookingDetailsCreate, user_id)
    → checks hotel exists
    → checks room exists
    → checks room is available (isavailable == True)
    → checks dates are valid (checkout > checkin, days > 0)
    → calculates amount = rate × days
    → sets room.isavailable = False
    → creates booking with status "confirmed"

update_booking_details(db, booking_id, status)    → changes status field
remove_booking_details(db, booking_id)            → sets status = "cancelled", frees room
show_all_booking_details(db)                      → all bookings (admin)
show_booking_details(db, booking_id)              → one booking
show_bookings_by_user(db, user_id)                → only this user's bookings
```

### PaymentService (`services/payment_service.py`)
```
add_payment(db, booking_id, payment_method)
    → checks booking exists
    → checks no payment already exists for this booking
    → creates Transaction row
    → creates Payment row linked to booking + transaction

show_all_payments(db)            → all payments (admin)
show_payment(db, payment_id)     → one payment with transaction details
```

### AdminService (`services/admin_service.py`)
```
create_admin(db, AdminCreate)    → checks email unique → hashes password → saves
sign_in(db, email, password)     → verifies credentials → returns JWT token
sign_out(admin)                  → returns success message (token invalidated client-side)
```

---

## 10. Repository Layer

Repository classes in `app/repositories/` are the only place SQLAlchemy queries run.
Services call repositories; routers never touch repositories directly.

### Why this pattern?

```
Without Repository:              With Repository:
─────────────────────────────    ──────────────────────────────────
router.py contains:              router.py calls:
  db.query(Hotel)                  hotel_service.show_hotel(id)
  .filter(Hotel.id == id)              ↓
  .first()                         hotel_service calls:
                                     hotel_repository.show_hotel(id)
                                         ↓ (DB query lives here)
                                     returns Hotel object
```

This makes it easy to:
- Change the database later (just change repository)
- Test services without a real DB (mock the repository)
- Reuse queries across multiple services

### Key repository behaviors

**`booking_repository.py` — cancel logic:**
```python
# Sets status to "cancelled" — does NOT delete the record
# Then sets room.isavailable = True so it can be booked again
```

**`payment_repository.py` — payment creation:**
```python
# Creates a Transaction first, gets its ID
# Then creates Payment linked to that transaction ID
# Both happen in one function call
```

---

## 11. All API Endpoints

**Base URL:** `http://localhost:8000`  
**Test them:** `http://localhost:8000/docs`

### Auth — no token needed
| Method | URL | What it does | Request body |
|---|---|---|---|
| POST | `/auth/register` | Register as customer | `{user_name, email, password}` |
| POST | `/auth/login` | Customer login | `{email, password}` |
| POST | `/auth/admin/register` | Create admin account | `{admin_name, email, password}` |
| POST | `/auth/admin/login` | Admin login | `{email, password}` |

All login endpoints return:
```json
{
  "access_token": "eyJ...",
  "token_type": "bearer",
  "role": "customer",
  "user_id": 1,
  "user_name": "John Doe"
}
```

---

### Hotels — public read, admin write
| Method | URL | Who | Notes |
|---|---|---|---|
| GET | `/hotels` | Anyone | Add `?city=Mumbai` to search |
| GET | `/hotels/{id}` | Anyone | Single hotel details |
| POST | `/hotels` | Admin only | Create hotel |
| PUT | `/hotels/{id}` | Admin only | Update hotel fields |
| DELETE | `/hotels/{id}` | Admin only | Deletes hotel + all its rooms |

---

### Rooms — public read, admin write
| Method | URL | Who | Notes |
|---|---|---|---|
| GET | `/rooms` | Anyone | Add `?hotel_id=1` to filter |
| GET | `/rooms/available` | Anyone | Requires `?hotel_id=1` |
| GET | `/rooms/{id}` | Anyone | Single room details |
| POST | `/rooms` | Admin only | Create room (hotel_id required) |
| PUT | `/rooms/{id}` | Admin only | Update room |
| DELETE | `/rooms/{id}` | Admin only | Delete room |

---

### Bookings — customer + admin
| Method | URL | Who | Notes |
|---|---|---|---|
| GET | `/bookings` | Admin only | All bookings in system |
| GET | `/bookings/my` | Customer | Only current user's bookings |
| GET | `/bookings/{id}` | Customer | Single booking |
| POST | `/bookings` | Customer | Creates booking (auto-calculates amount) |
| PUT | `/bookings/{id}/cancel` | Customer | Cancels booking, frees room |
| PUT | `/bookings/{id}/status` | Admin only | Change status manually |

**POST /bookings request body:**
```json
{
  "hotel_id": 1,
  "room_id": 3,
  "booked_from": "2025-08-01",
  "booked_to": "2025-08-05",
  "no_of_adults": 2,
  "no_of_children": 0
}
```

---

### Payments
| Method | URL | Who | Notes |
|---|---|---|---|
| GET | `/payments` | Admin only | All payments with transaction details |
| GET | `/payments/{id}` | Customer | Single payment |
| POST | `/payments` | Customer | Make payment for a booking |

**POST /payments request body:**
```json
{
  "booking_id": 1,
  "payment_method": "online"
}
```
> `payment_method` options: `online`, `card`, `upi`, `cash`

---

### Users
| Method | URL | Who | Notes |
|---|---|---|---|
| GET | `/users` | Admin only | All registered customers |
| GET | `/users/{id}` | Customer | Get profile |
| PUT | `/users/{id}` | Customer | Update name/mobile/address |
| DELETE | `/users/{id}` | Admin only | Delete customer account |

---

## 12. Frontend Pages & Routes

### URL → Page mapping

| URL | Page File | Who can access | What it shows |
|---|---|---|---|
| `/` | — | Anyone | Redirects to `/hotels` |
| `/hotels` | `HotelsPage.jsx` | Anyone | Hotel cards grid + city search |
| `/hotels/:id` | `HotelDetailPage.jsx` | Anyone | Hotel info + all rooms |
| `/login` | `LoginPage.jsx` | Not logged in | Login form with Customer/Admin toggle |
| `/register` | `RegisterPage.jsx` | Not logged in | Registration form |
| `/book/:roomId` | `BookingPage.jsx` | Customer | Date picker, guest count, price preview |
| `/payment/:bookingId` | `PaymentPage.jsx` | Customer | Payment method + confirm pay |
| `/my-bookings` | `BookingHistoryPage.jsx` | Customer | List of own bookings + cancel |
| `/admin/dashboard` | `AdminDashboardPage.jsx` | Admin | Stats cards + recent bookings |
| `/admin/hotels` | `AdminHotelsPage.jsx` | Admin | Hotel table + Add/Edit/Delete modals |
| `/admin/rooms` | `AdminRoomsPage.jsx` | Admin | Room table with hotel filter + modals |
| `/admin/bookings` | `AdminBookingsPage.jsx` | Admin | All bookings with status filter |
| `/admin/users` | `AdminUsersPage.jsx` | Admin | All users + delete |
| `/admin/payments` | `AdminPaymentsPage.jsx` | Admin | All payments + revenue total |

### How route protection works

```javascript
// src/components/common/ProtectedRoute.jsx

ProtectedRoute          → checks: is user logged in?
                           No  → redirect to /login
                           Yes → show the page

ProtectedRoute adminOnly → checks: is user logged in AND role === "admin"?
                           No  → redirect to / 
                           Yes → show the page

PublicOnlyRoute          → checks: is user NOT logged in?
                           Already logged in → redirect to /hotels (customer) or /admin/dashboard (admin)
                           Not logged in → show the page (login/register)
```

### How auth state is shared across pages

```javascript
// src/context/AuthContext.jsx

// What's stored in localStorage:
//   hbs_token  → JWT string (attached to every API call)
//   hbs_user   → {access_token, role, user_id, user_name} object

// Any page can use:
const { user, login, logout, isAdmin } = useAuth()
```

---

## 13. How Auth Works (JWT Flow)

### Registration
```
1. User fills form → RegisterPage calls authService.register()
2. POST /auth/register → password is bcrypt-hashed → saved to DB
3. User redirected to /login
```

### Login
```
1. User fills email + password → LoginPage calls authService.login()
2. POST /auth/login:
   a. Find user by email in DB
   b. Verify password using bcrypt.verify(input, hashed)
   c. If match: create JWT = {sub: user_id, role: "customer", exp: +24h}
   d. Return {access_token, role, user_id, user_name}
3. Frontend saves to localStorage (hbs_token, hbs_user)
4. All future API calls: Axios adds "Authorization: Bearer <token>"
```

### Protected API call
```
1. Frontend calls e.g. bookingService.create()
2. api.js interceptor adds header: Authorization: Bearer eyJ...
3. FastAPI router has: _=Depends(require_customer)
4. require_customer() decodes the JWT:
   a. Valid & not expired → extracts user_id and role
   b. Invalid or expired  → returns HTTP 401
5. If 401 → api.js interceptor clears localStorage, redirects to /login
```

### JWT structure
```
Header:  { "alg": "HS256", "typ": "JWT" }
Payload: { "sub": "5", "role": "customer", "exp": 1234567890 }
Signed with SECRET_KEY from .env file
```

---

## 14. Sprint Roadmap

### Sprint 1 — Core Models + Repository (Pure Python / Java)
- [ ] Define all 7 entity classes (User, Admin, Hotel, Room, Booking, Transaction, Payment)
- [ ] Set up ORM mappings (SQLAlchemy / Hibernate)
- [ ] Implement all 6 repository classes with CRUD methods
- [ ] Write unit tests for all repositories
- [ ] Validate that data saves and retrieves correctly

### Sprint 2 — FastAPI Backend + REST API
- [ ] Set up FastAPI project structure (routers, services, schemas)
- [ ] Implement all 6 service classes with business logic
- [ ] Create all REST endpoints (auth, hotels, rooms, bookings, payments, users)
- [ ] Add JWT authentication (register → login → protected routes)
- [ ] Test all endpoints via Swagger at `/docs`
- [ ] Test with Postman collection

### Sprint 3 — React Frontend + Integration
- [ ] Set up React + Vite project with React Router
- [ ] Build all customer pages (hotels, booking, payment, history)
- [ ] Build all admin pages (dashboard, CRUD modals)
- [ ] Connect all pages to backend via Axios services
- [ ] Add route guards (ProtectedRoute, PublicOnlyRoute)
- [ ] End-to-end testing: register → browse → book → pay

---

## 15. TDD — Test Cases per User Story

### How to approach TDD
```
Step 1: Write the test first (it will FAIL — that's expected)
Step 2: Write minimum code to make the test PASS (Green)
Step 3: Clean up code without breaking tests (Refactor)
```

### US-001 — User Registration
```
✅ Register with valid data                    → 200, user_id returned
✅ Register with duplicate email               → 400, "Email already registered"
✅ Register without user_name                  → 422, validation error
✅ Register without password                   → 422, validation error
✅ Register with invalid email format          → 422, validation error
```

### US-002 — User Login
```
✅ Login with correct email + password         → 200, JWT token returned
✅ Login with wrong password                   → 401, "Invalid email or password"
✅ Login with email that doesn't exist         → 401
✅ JWT token contains correct role             → token.role == "customer"
```

### US-003 — View Hotels (customer)
```
✅ GET /hotels returns list                    → 200, array of hotels
✅ GET /hotels?city=Mumbai returns filtered    → only hotels in Mumbai
✅ GET /hotels/{valid_id} returns hotel        → 200, hotel object
✅ GET /hotels/{invalid_id}                    → 404, "Hotel not found"
```

### US-004/005/006 — Hotel CRUD (admin)
```
✅ Admin creates hotel                         → 200, hotel_id returned
✅ Customer tries to create hotel              → 403, "Admin access required"
✅ Admin updates hotel city                    → 200, city value changed in DB
✅ Admin deletes hotel                         → 200, hotel gone from list
✅ Delete hotel also removes its rooms         → rooms with that hotel_id gone
```

### US-007 — View Rooms
```
✅ GET /rooms?hotel_id=1 returns rooms         → 200, rooms for that hotel
✅ GET /rooms/available?hotel_id=1             → only rooms where isavailable=True
✅ GET /rooms/{id} returns room details        → 200, room object
```

### US-008/009/010 — Room CRUD (admin)
```
✅ Admin adds room to valid hotel              → 200, room_id returned
✅ Admin adds room to non-existent hotel       → 404, "Hotel not found"
✅ Admin updates room rate                     → 200, rate changed
✅ Admin toggles isavailable                   → boolean flipped
✅ Admin deletes room                          → 200, room gone
```

### US-011 — Book Room
```
✅ Book available room with valid dates        → 200, booking confirmed
✅ Book already-booked room                    → 400, "Room is not available"
✅ Book with checkout before checkin           → 400, "Invalid booking dates"
✅ Amount = rate_per_day × nights              → verified in response
✅ Room.isavailable becomes False after booking → confirmed via GET /rooms/{id}
```

### US-012 — View Booking History
```
✅ GET /bookings/my shows only my bookings     → user_id matches logged-in user
✅ Admin GET /bookings shows all bookings      → all users' bookings returned
✅ GET /bookings/my with no bookings           → returns empty array []
```

### US-013 — Cancel Booking
```
✅ Cancel confirmed booking                    → status = "cancelled"
✅ Room is available again after cancel        → room.isavailable = True
✅ Cancel non-existent booking                 → 404, "Booking not found"
```

### US-014 — Make Payment
```
✅ Pay for confirmed booking                   → 200, payment_id returned
✅ Transaction created with correct amount     → transaction.amount == booking.amount
✅ Pay for same booking twice                  → 400, "Payment already made"
✅ Pay for non-existent booking                → 404, "Booking not found"
```

### US-015 — View Payments (admin)
```
✅ Admin GET /payments returns all payments    → 200, array with transaction details
✅ Total revenue = sum of all transaction.amount
```

### US-016 — Manage Customers (admin)
```
✅ Admin GET /users returns all customers      → 200
✅ Admin DELETE /users/{id} removes customer   → user gone from list
✅ Customer tries GET /users                   → 403
```

### US-017 — Admin Dashboard
```
✅ Dashboard shows correct hotel count
✅ Dashboard shows correct booking count
✅ Dashboard shows correct user count
✅ Revenue matches sum of payments
✅ Recent bookings shows last 5 bookings
```

---

## 16. Design Patterns Used

| Pattern | Where | What it means simply |
|---|---|---|
| **Layered Architecture** | Whole backend | Each layer only talks to the layer below it. Router → Service → Repository → DB |
| **Repository Pattern** | `repositories/` | All DB queries in one place. Services never touch the DB directly |
| **Service Layer** | `services/` | Business rules live here. Services don't know about HTTP or DB format |
| **DTO / Schema Pattern** | `schemas/` | Input and output shapes are separate from the DB model. Pydantic validates them |
| **Dependency Injection** | FastAPI `Depends()` | DB session and auth checks are "injected" into route functions automatically |
| **Singleton** | Service/repo instances | Each service/repo class is instantiated once at module level, reused everywhere |
| **Context API** | `AuthContext.jsx` | Global React state without passing props through every component |
| **Protected Route** | `ProtectedRoute.jsx` | HOC pattern — wraps a page component to add auth check |
| **Interceptor** | `api.js` | Axios interceptor attaches JWT to every request automatically |

---

## 17. Common Errors & Fixes

| Error | Cause | Fix |
|---|---|---|
| `ModuleNotFoundError: No module named 'app'` | Running uvicorn from wrong directory | `cd backend` first, then run uvicorn |
| `422 Unprocessable Entity` | Request body is missing a required field | Check the Swagger docs for required fields |
| `401 Unauthorized` | Token missing or expired | Log in again to get a fresh token |
| `403 Forbidden` | Logged in as customer, trying admin endpoint | Use admin account |
| `400 Room is not available` | Room was already booked | Choose a different room or check availability |
| `400 Payment already made` | Tried to pay for same booking twice | Go to /my-bookings to see your booking |
| `CORS error in browser` | Frontend port not in CORS allowed list | Check `main.py` — `allow_origins` should include `http://localhost:5173` |
| `npm install` fails | Old Node.js version | Update Node.js to v18 or later |
| `venv\Scripts\activate` fails | Wrong shell | Use Command Prompt or PowerShell, not Git Bash |
| DB shows old data after model change | SQLite cached schema | Delete `hotel_booking.db` and restart backend (all data resets) |

---

*Navikenz Internship Capstone — Hotel Booking Management System*  
*FastAPI 0.111 + React 18 + SQLAlchemy 2.0 + JWT + SQLite*
