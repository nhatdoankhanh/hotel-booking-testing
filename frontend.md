# Frontend — What's What and Why

The frontend is a **React single-page app** built with **Vite**. It has no business logic of its own — every page just displays data and sends HTTP requests to the backend API at `http://localhost:8000`. This file walks through every folder and explains what each piece is responsible for.

**Stack:** React 18 · React Router 6 · Axios · Vite. That's it — no Redux, no TypeScript, no CSS framework. Deliberately kept small so there's exactly one way to do each thing.

---

## Folder structure

```
frontend/src/
├── main.jsx                  Entry point — mounts <App /> into the page
├── App.jsx                   All routes are declared here
├── index.css                 Global styles (buttons, cards, badges, forms)
├── context/
│   └── AuthContext.jsx        Holds "who is logged in" for the whole app
├── components/common/
│   ├── Navbar.jsx              Top navigation bar, changes based on role
│   └── ProtectedRoute.jsx      Route guards (login required / admin required)
├── services/                  One file per feature — wraps every Axios call
│   ├── api.js                   The shared Axios instance (used by all others)
│   ├── authService.js
│   ├── hotelService.js
│   ├── roomService.js
│   ├── bookingService.js
│   ├── paymentService.js
│   └── userService.js
└── pages/                     One component per screen (13 total)
    ├── LoginPage.jsx, RegisterPage.jsx
    ├── HotelsPage.jsx, HotelDetailPage.jsx
    ├── BookingPage.jsx, BookingHistoryPage.jsx, PaymentPage.jsx
    └── AdminDashboardPage.jsx, AdminHotelsPage.jsx, AdminRoomsPage.jsx,
        AdminBookingsPage.jsx, AdminUsersPage.jsx, AdminPaymentsPage.jsx
```

**Rule of thumb:** a page never calls Axios directly — it always goes through a `services/*.js` file. That way, if an API endpoint's URL or shape ever changes, there's exactly one place to fix it per feature.

---

## How a click turns into a rendered page (the request flow)

```
User clicks a button in a Page component
   → Page calls a function from services/xyzService.js
       → that function calls the shared `api` (Axios instance) from services/api.js
           → api.js automatically attaches "Authorization: Bearer <token>" if logged in
               → request goes out to http://localhost:8000/...
                   → response (JSON) comes back
               → Page's useState is updated with the response data
           → React re-renders the page with the new data
```

Nothing more complicated than that anywhere in the app — every single page follows this exact same pattern.

---

## `services/api.js` — the one Axios instance everything shares

```js
const api = axios.create({ baseURL: 'http://localhost:8000', ... })
```

Two automatic behaviors are attached here, so no page has to think about them:

1. **Request interceptor** — before every outgoing request, it reads the JWT token from `localStorage` and attaches it as `Authorization: Bearer <token>`. This is why pages never manually add auth headers.
2. **Response interceptor** — if any response comes back `401 Unauthorized` (expired or invalid token), it clears the stored user/token and redirects the browser to `/login`. This is why an expired session never leaves you stuck on a broken page.

## `services/*Service.js` — one file per backend feature

Each file is a thin wrapper, e.g. `hotelService.js`:

```js
export const hotelService = {
  getAll: (city) => api.get('/hotels', { params: city ? { city } : {} }),
  getById: (id) => api.get(`/hotels/${id}`),
  create: (data) => api.post('/hotels', data),
  update: (id, data) => api.put(`/hotels/${id}`, data),
  delete: (id) => api.delete(`/hotels/${id}`),
}
```

There are 6 of these (`auth`, `hotel`, `room`, `booking`, `payment`, `user`), each mapping 1:1 to the backend's REST endpoints for that feature. If you want to know exactly which URL a button calls, this is the first place to look.

---

## `context/AuthContext.jsx` — "who is logged in", shared everywhere

React's built-in **Context API** (no external library) holds one thing: the logged-in user object (or `null`). It's created once in `App.jsx` (`<AuthProvider>` wraps the whole app) and read anywhere with the `useAuth()` hook.

```js
const { user, login, logout, isAdmin } = useAuth()
```

- `login(userData)` — saves the user + token to `localStorage` (so a page refresh doesn't log you out) and updates React state.
- `logout()` — clears both.
- `isAdmin` — a convenience boolean (`user?.role === 'admin'`), used everywhere to decide what to show.
- On first load, a `useEffect` checks `localStorage` for a saved session and restores it — this is how "stay logged in after refresh" works.

## `components/common/ProtectedRoute.jsx` — route guards

Two small wrapper components, used directly in `App.jsx`'s route list:

- **`<ProtectedRoute>`** — if nobody's logged in, redirect to `/login`. If `adminOnly` is set and the logged-in user isn't an admin, redirect away.
- **`<PublicOnlyRoute>`** — the opposite: if you're *already* logged in and you land on `/login` or `/register`, it redirects you straight to your dashboard/hotels page instead of showing the form again.

## `components/common/Navbar.jsx`

One navigation bar, always visible (rendered once in `App.jsx`, above the routed page). It shows **different links depending on who's logged in**:
- Nobody logged in → Hotels / Login / Register
- Customer logged in → Hotels / My Bookings / Logout
- Admin logged in → Dashboard / Hotels / Rooms / Bookings / Users / Payments / Logout

---

## `App.jsx` — every route in one place

```jsx
<Route path="/hotels" element={<HotelsPage />} />                                  // public
<Route path="/login" element={<PublicOnlyRoute><LoginPage /></PublicOnlyRoute>} /> // logged-out only
<Route path="/my-bookings" element={<ProtectedRoute><BookingHistoryPage /></ProtectedRoute>} /> // any logged-in user
<Route path="/admin/dashboard" element={<ProtectedRoute adminOnly><AdminDashboardPage /></ProtectedRoute>} /> // admin only
```

Reading this file top to bottom tells you exactly which URL shows which page, and what access level it needs — nothing is hidden in some separate routing config.

---

## The pages, one by one

### Customer-facing

| Page | What it does |
|---|---|
| `LoginPage.jsx` | Email/password form with a Customer/Admin toggle. Calls `authService.login` or `authService.adminLogin` depending on the toggle, then `login(data)` from `AuthContext` to store the session. |
| `RegisterPage.jsx` | Simple sign-up form → `authService.register` → redirects to login on success. |
| `HotelsPage.jsx` | Fetches all hotels (`hotelService.getAll`) on load; the search box re-fetches filtered by city. Clicking a card navigates to its detail page. |
| `HotelDetailPage.jsx` | Fetches one hotel + its rooms in parallel (`Promise.all`). Shows a "Book Now" button per available room (only if you're logged in as a customer). |
| `BookingPage.jsx` | Loads the room + hotel, then a date-picker form. A `useEffect` recalculates the live price total (`nights × rate`) every time the dates change — this is a *preview only*; the backend recalculates the real amount itself and never trusts what the browser sends. On submit, calls `bookingService.create` and moves to the payment page. |
| `BookingHistoryPage.jsx` | Fetches `bookingService.getMy()` — the backend already filters this to *your* bookings only, based on your JWT. Shows Pay/Cancel buttons on unpaid bookings. |
| `PaymentPage.jsx` | Loads the booking + hotel, lets you pick a payment method, calls `paymentService.makePayment`, then shows a success screen. |

### Admin-facing

| Page | What it does |
|---|---|
| `AdminDashboardPage.jsx` | Calls 4 existing endpoints in parallel (`hotels`, `bookings`, `users`, `payments`) and computes stat-card numbers and revenue **in the browser** — there's no dedicated backend "dashboard" endpoint, it's just reusing what already exists. |
| `AdminHotelsPage.jsx` | Table of all hotels + an "Add/Edit Hotel" modal form. Same component handles both add and edit (`editing` state decides which). |
| `AdminRoomsPage.jsx` | Same pattern as hotels, plus a "Filter by Hotel" dropdown and a Hotel-picker inside the form (since every room belongs to exactly one hotel). |
| `AdminBookingsPage.jsx` | Read-only table of every booking across all customers, with a status filter (all/confirmed/cancelled/completed). |
| `AdminUsersPage.jsx` | Table of every registered customer, with a Delete action. |
| `AdminPaymentsPage.jsx` | Table of every payment, with a running "Total Revenue" figure computed the same way the dashboard does. |

---

## Styling approach

There's no CSS framework (no Bootstrap/Tailwind). Shared, reusable classes (`.btn`, `.card`, `.badge`, `.form-group`, `.page-wrapper`) live in `index.css`. One-off layout tweaks are written as inline `style={{...}}` objects directly in each component — you'll see this a lot; it's intentional so you never have to jump to a separate file to see how something is positioned.
