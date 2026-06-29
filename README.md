# Hotel Booking Management System

A full-stack hotel booking platform built as a capstone project for the **Navikenz Internship**. Customers can browse hotels, book rooms, and make payments. Admins get a complete management dashboard.

**Stack:** FastAPI · React · SQLite · JWT Auth

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

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | FastAPI 0.111, Python 3.10+ |
| Database | SQLite (auto-created, no setup) |
| ORM | SQLAlchemy 2.0 |
| Auth | JWT via `python-jose`, bcrypt via `passlib` |
| Frontend | React 18, Vite 5, React Router 6 |
| HTTP Client | Axios |

---

## Project Structure

```
Hotel_Booking_system/
├── backend/
│   ├── .env.example          ← Copy to .env and fill in your SECRET_KEY
│   ├── requirements.txt
│   └── app/
│       ├── main.py           ← FastAPI entry point, CORS config
│       ├── database.py       ← SQLite connection & session factory
│       ├── core/             ← JWT, bcrypt, auth guards (Depends)
│       ├── models/           ← SQLAlchemy ORM models (7 tables)
│       ├── schemas/          ← Pydantic request/response schemas
│       ├── repositories/     ← All DB queries (repository pattern)
│       ├── services/         ← Business logic layer
│       └── routers/          ← REST endpoints
│
└── frontend/
    ├── package.json
    └── src/
        ├── context/          ← AuthContext — global JWT state
        ├── services/         ← Axios API wrappers (one per entity)
        ├── components/       ← Navbar, ProtectedRoute
        └── pages/            ← 13 pages (customer + admin)
```

---

## Getting Started

### Prerequisites

- Python 3.10+
- Node.js 18+

### 1. Clone the repo

```bash
git clone https://github.com/YOUR_USERNAME/Hotel_Booking_system.git
cd Hotel_Booking_system
```

### 2. Backend setup

```bash
cd backend

# Create and activate virtual environment
python -m venv .venv
.venv\Scripts\activate        # Windows
# source .venv/bin/activate   # macOS / Linux

# Install dependencies
pip install -r requirements.txt

# Set up environment variables
copy .env.example .env        # Windows
# cp .env.example .env        # macOS / Linux
# Edit .env and set a strong SECRET_KEY

# Start the server
uvicorn app.main:app --reload --port 8000
```

Backend runs at `http://localhost:8000`  
Swagger docs at `http://localhost:8000/docs`

### 3. Create the first admin account

Open `http://localhost:8000/docs`, find `POST /auth/admin/register`, and send:

```json
{
  "admin_name": "Admin",
  "email": "admin@hotel.com",
  "password": "admin123"
}
```

> Only needed once — data is saved in `hotel_booking.db`.

### 4. Frontend setup

Open a new terminal:

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at `http://localhost:5173`

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

Full interactive docs: `http://localhost:8000/docs`

---

## Architecture

```
React Frontend (localhost:5173)
        │  JWT in Authorization header (Axios)
        ▼
FastAPI Backend (localhost:8000)
  ┌─ Routers    ← HTTP endpoints + input validation (Pydantic)
  ├─ Services   ← Business rules, availability checks, amount calc
  ├─ Repos      ← All SQLAlchemy queries (repository pattern)
  └─ Models     ← ORM table definitions
        │
        ▼
SQLite Database (hotel_booking.db)
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

## Environment Variables

Copy `backend/.env.example` to `backend/.env`:

```env
SECRET_KEY=your_strong_secret_key_here
ALGORITHM=HS256
ACCESS_TOKEN_EXPIRE_MINUTES=1440
```

> Never commit `.env` — it is already in `.gitignore`.

---

## Design Patterns

| Pattern | Where used |
|---|---|
| Layered Architecture | Router → Service → Repository → DB |
| Repository Pattern | `repositories/` — single source for all DB queries |
| DTO / Schema Pattern | `schemas/` — Pydantic separates API shape from DB model |
| Dependency Injection | FastAPI `Depends()` for DB sessions and auth guards |
| Context API | `AuthContext.jsx` — global JWT state across all React pages |
| Interceptor Pattern | `api.js` — Axios auto-attaches JWT to every request |

---

*Navikenz Internship Capstone — Hotel Booking Management System*
