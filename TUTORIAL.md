# Tutorial — Running the Hotel Booking System

This is a plain, step-by-step walkthrough: how to start the project, and then how to actually use it — once as an **Admin**, once as a **Customer**. Follow it in order; later steps depend on data created in earlier ones.

---

## 1. Prerequisites

You need two things installed:

- **Java 17** (check with `java -version`)
- **Node.js 18+** (check with `node -v`)

Nothing else — no database to install (H2 creates its own file) and no Maven to install (a wrapper script, `mvnw`, is already in the project).

---

## 2. Start the backend

Open a terminal:

```bash
cd Hotel_booking_system/backend
./mvnw spring-boot:run
```

Wait for a line like `Started HotelbookingApplication`. The API is now live at **http://localhost:8000**, and a file `backend/hotel_booking.mv.db` has been created automatically — that's your entire database.

Leave this terminal open and running.

---

## 3. Start the frontend

Open a **second** terminal:

```bash
cd Hotel_booking_system/frontend
npm install     # first time only
npm run dev
```

Open **http://localhost:5173** in your browser — that's the app.

---

## 4. Create the first Admin account

Nobody is an admin by default — you create that account yourself, once, using Swagger (the API's interactive docs page):

1. Go to **http://localhost:8000/swagger-ui/index.html**
2. Find `POST /auth/admin/register` → click **Try it out**
3. Paste this (or use your own values):
   ```json
   {
     "admin_name": "Admin",
     "email": "admin@hotel.com",
     "password": "admin123"
   }
   ```
4. Click **Execute**. You should get back `{"message": "Admin registered", "admin_id": 1}`.

Remember that email and password — that's your admin login from now on.

---

## 5. Walkthrough as Admin

1. Go to `http://localhost:5173/login`.
2. Click the **Admin** toggle at the top of the login form.
3. Enter the email/password from Step 4 → **Sign In as Admin**.
4. You land on the **Admin Dashboard** — everything shows 0 right now.

### Add a hotel
Go to **Hotels** (admin nav) → **+ Add Hotel**. Fill in:

| Field | Required | Example |
|---|---|---|
| Hotel Name | Yes | Taj Palace |
| City | Yes | Mumbai |
| Avg Rate/Night (₹) | Yes | 5000 |
| Email, Phone 1, Phone 2, Website, Address, Description | No | (optional) |

Click **Add Hotel**. It appears in the table immediately.

### Add a room to that hotel
Go to **Rooms** → **+ Add Room**. Fill in:

| Field | Required | Example |
|---|---|---|
| Hotel | Yes | pick the one you just made |
| Room Number | Yes | 101 |
| Room Type | Yes | Deluxe / Standard / Suite / etc. |
| Rate per Night (₹) | Yes | 2000 — **this** is the price customers actually pay, not the hotel's average rate |
| Available | No | defaults to Yes |

Click **Add Room**.

### Check the dashboard
Go back to **Dashboard** — "Total Hotels" should now read 1. (Rooms aren't counted separately on the dashboard, only hotels/bookings/users/revenue.)

### Other admin screens
- **Bookings** — every booking made by every customer, filterable by status (all / confirmed / cancelled / completed).
- **Users** — every registered customer; you can delete an account here.
- **Payments** — every payment made, plus a running "Total Revenue" figure at the top.

Admins can view all of this but the actual *booking* and *paying* only happens from a customer account — see the next section.

---

## 6. Walkthrough as Customer

Log out of the admin account first (top-right **Logout**).

### Register
1. Go to `http://localhost:5173/register`.
2. Fill in Full Name, Email, Password (mobile/address optional) → **Create Account**.
3. You're redirected to the login page.

### Log in
Log in with that email/password — make sure the **Customer** toggle is selected (it's the default).

### Browse and book
1. Go to **Hotels** — you'll see the hotel the admin added. You can also search by city.
2. Click a hotel card → see its rooms.
3. Click **Book Now** on an available room.
4. Pick a **Check-In** and **Check-Out** date and the number of adults/children. The total price updates live as you pick dates (rate × nights).
5. Click **Confirm Booking & Proceed to Payment**.

### Pay
You're taken straight to the payment page for that booking:
1. Review the summary (hotel, dates, guests, total).
2. Pick a payment method (Online Banking / Card / UPI / Cash).
3. Click **Pay ₹...**. You'll see a "Payment Successful" confirmation.

### View and cancel bookings
Go to **My Bookings**:
- Every booking you've made is listed with its status.
- A `confirmed` booking that hasn't been paid yet shows **Pay Now** and **Cancel** buttons.
- Clicking **Cancel** sets its status to `cancelled` and frees the room back up — go check the hotel's room list again and you'll see it's bookable by someone else.

---

## 7. What to try to prove it all works end-to-end

- Book the *same* room twice in a row (without cancelling) → the second attempt should fail with "Room is not available".
- Try to pay for the same booking twice → second attempt should fail with "Payment already made for this booking".
- Log in as a second customer and check **My Bookings** → you should only ever see your own bookings, never someone else's.
- As a customer, try visiting `/admin/dashboard` directly in the URL bar → you should get bounced back, since that page is admin-only.

---

## 8. Stopping everything

`Ctrl+C` in both terminals (backend and frontend). Your data stays in `backend/hotel_booking.mv.db` for next time — nothing is lost between runs unless you delete that file.
