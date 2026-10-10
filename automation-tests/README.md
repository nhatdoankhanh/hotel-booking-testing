# Automation Testing — Hotel Booking System

This project provides automated UI tests for a hotel booking website using **Java 17, Selenium WebDriver, TestNG and Maven**. Tests interact with the browser through customer and administrator workflows, including authentication, hotel search, booking, payment and hotel management.

Original website: [khusi2707/Hotel_booking_system](https://github.com/khusi2707/Hotel_booking_system). The automation tests and testing documents are additions to that website. Credit for the original application belongs to its author.

## Requirements and Test Design Documents

The automation project accompanies three documents I created:

- [Requirement Analysis](../docs/testing/Req-als.xlsx): analyzes requirements to define the testing scope.
- [Test Scenarios](../docs/testing/Test-scenario.xlsx): organizes the situations and user workflows to test.
- [Test Cases](../docs/testing/Test-case.xlsx): turns scenarios into detailed, executable test cases.

The workflow is **Requirement Analysis → Test Scenarios → Test Cases → UI Automation**. The suite automates selected cases from the testing scope. See the [testing documentation overview](../docs/testing/README.md) for the complete introduction to the QA work.

## 1. Test Coverage

| Group | Test Cases | Coverage |
| --- | ---: | --- |
| Authentication | 6 | Registration, duplicate email, Customer/Admin login, incorrect password and session persistence after refresh |
| Hotel Search | 4 | Hotel listing, city search, case-insensitive search and partial city search |
| Booking | 7 | Booking creation, one-night/multiple-night amounts, invalid dates, My Bookings, cancellation and room availability after cancellation |
| Payment | 3 | Successful payment, correct Booking ID and payment records in the Admin UI |
| End-to-End | 2 | Search → book → pay → view booking; cancel → room available → reopen booking form |
| Admin Hotel Management | 3 | Add, update and delete hotels |
| **Total** | **25** | **UI tests** |

Tests validate results displayed in the UI. The suite does not include independent API, performance or security testing. The website uses simulated payments; these tests do not verify transactions with a real payment gateway. This table describes automated coverage and does not imply that every requirement in the Excel documents has been tested.

## 2. Project Structure

```text
automation-tests/
├── pom.xml
├── README.md
└── src/
    ├── main/java/com/hotelbooking/
    │   ├── pages/                 # Locators and UI actions, including Admin pages
    │   └── utils/                 # ConfigReader, WaitUtil
    └── test/
        ├── java/com/hotelbooking/
        │   ├── base/              # BaseJavaTest: browser setup and teardown
        │   ├── tests/authentication/
        │   ├── hotel/
        │   ├── booking/
        │   ├── payment/
        │   └── e2e/
        └── resources/
            ├── config.example.properties
            ├── config.properties      # Local file, excluded from Git
            ├── testng.xml
            ├── payment-testng.xml
            └── e2e-testng.xml
```

The code follows the Page Object Model. Page classes contain locators and UI actions; test classes contain scenarios and assertions. `BaseJavaTest` creates a new browser for each test case and closes it afterward. `WaitUtil` uses explicit waits for elements and page states with a 10-second timeout.

## 3. Environment Setup

- JDK 17 and Maven available in `PATH`.
- Node.js 18 or later to run the frontend.
- Chrome installed locally. The code also supports Edge and Firefox; the recorded results below were obtained with Chrome.
- Internet access on the first run to download dependencies and resolve the appropriate driver through Selenium Manager.

Check the required tools:

```powershell
java -version
mvn -version
node -v
npm -v
```

Open two separate terminals from the repository root to start the website.

**Backend terminal (Windows):**

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

**Frontend terminal:**

```powershell
cd frontend
npm install
npm run dev
```

Default backend URL: `http://localhost:8000`. Default frontend URL: `http://localhost:5173`. For application setup and initial Admin account creation, see the [website README](../README.md#getting-started) and the [original repository](https://github.com/khusi2707/Hotel_booking_system).

## 4. Configuration and Test Data

Create your local configuration from [config.example.properties](src/test/resources/config.example.properties). From the `automation-tests` directory, run:

```powershell
Copy-Item src/test/resources/config.example.properties src/test/resources/config.properties
```

Edit `src/test/resources/config.properties` for your environment and supply your test account credentials. The local file is excluded from Git. `ConfigReader` reads it directly; update `baseUrl` in the file when using another frontend address.

| Key | Description |
| --- | --- |
| `baseUrl` | Frontend URL |
| `browser` | `chrome`, `edge` or `firefox` |
| `customerEmail`, `customerPassword` | Existing Customer test account |
| `adminEmail`, `adminPassword` | Existing Admin test account |
| `wrongPassword` | Incorrect password for the failed-login test |
| `fullName`, `password` | Name and password used to register test accounts |
| `hotelCity` | City containing hotels for search tests |
| `hotelName` | Hotel name included in the current configuration |
| `bookingHotel`, `bookingRoom` | Hotel and room used by Booking, Payment and E2E tests |
| `hotelName1`, `hotelName2` | Hotel names used by add/update/delete tests |
| `hotelCity1`, `hotelCity2`, `avgRate1`, `avgRate2` | Cities and rates used by Hotel Management tests |

Before running the suite:

1. Confirm that the Customer and Admin test accounts can log in.
2. Confirm that `bookingHotel` and `bookingRoom` exist, their names match the UI and the room is **Available**.
3. Ensure that `hotelCity` has hotel data for search tests.
4. Use separate hotel data for Hotel Management tests. Do not use the hotel containing the booking room as deletion test data.

Booking dates are generated relative to the execution date, so fixed dates are not required in the configuration. Booking, Payment and E2E tests cancel their bookings through the UI during cleanup to release the room. Some data, including registered accounts, payment records and added/updated hotels, may remain. Run the suite in a dedicated test environment. Before publishing the repository, replace personal credentials with placeholders and ask users to supply their own test accounts.

## 5. Running Tests

Keep the backend and frontend running. Open a third terminal from the repository root:

```powershell
cd automation-tests
mvn "-Dsurefire.suiteXmlFiles=src/test/resources/testng.xml" test
```

The suite runs sequentially: Authentication → Hotel Search → Booking → Payment → E2E → Admin Hotel Management. Keep parallel execution disabled because the tests share room and test data.

**Run Payment tests only:**

```powershell
mvn "-Dsurefire.suiteXmlFiles=src/test/resources/payment-testng.xml" test
```

**Run E2E tests only:**

```powershell
mvn "-Dsurefire.suiteXmlFiles=src/test/resources/e2e-testng.xml" test
```

**Run a single test class, such as Booking:**

```powershell
mvn "-Dtest=com.hotelbooking.booking.BookingTest" test
```

In IntelliJ IDEA, import `automation-tests/pom.xml` as a Maven project, select JDK 17, then right-click `src/test/resources/testng.xml` and select **Run**. If the terminal does not recognize `mvn`, install Maven and add it to `PATH`, or run the suite in IntelliJ.

`mvn test` also discovers classes whose names end in `Test`. Use the explicit suite command above to preserve the order defined in `testng.xml`.

## 6. End-to-End Flows

### E2E 1 — Search, Book, Pay and View My Bookings

`TC_E2E_UI_001_searchBookPayAndViewMyBookings`

Login Customer → Browse Hotels → Search city → View Rooms → Book Now → enter Check-In/Check-Out → Confirm Booking → Payment → Pay → Payment Successful → View My Bookings.

The test reads the selected hotel's city from the UI, searches for hotels, books a room and checks that Payment Successful displays the correct Booking ID and amount. After clicking View My Bookings, it verifies the new booking's dates, status and amount.

### E2E 2 — Cancel a Booking and Reopen the Room's Booking Form

`TC_E2E_UI_002_cancelBookingAndBookRoomAgain`

Login Customer → View Rooms → Book Now → enter dates → Confirm Booking → My Bookings → Cancel → Hotels → View Rooms → room Available → Book Now.

The test checks that the booking becomes Cancelled, the room becomes available again and Book Now opens the correct room's booking form. The flow ends at that form; it does not create or pay for a second booking.

## 7. Reports and Recorded Results

After a Maven run, reports are available in `target/surefire-reports/`. Read the `.txt` files for test totals and `TEST-*.xml` files for details. When running through IntelliJ, view results in the Run window.

Local results recorded on **October 10, 2026**, with the frontend/backend running on localhost and Chrome as the browser:

```text
Tests run: 25, Failures: 0, Errors: 0, Skipped: 0
Time elapsed: 70.21 s
```

These results reflect the recorded execution and its test data. Run the suite again after changing the environment or data to confirm the current results.

## 8. Troubleshooting

| Issue | Check |
| --- | --- |
| Website does not open | Backend/frontend are running, and the configured URL and port are correct |
| Login fails | Account exists, password is correct and the Customer/Admin role matches |
| Hotel or room cannot be found | Configured names match the UI, records exist and the room is Available |
| Booking times out | Room status, bookings left from previous runs and application response time |
| Driver does not start | Browser installation, driver compatibility and internet access for the initial download |
| Suite cannot find a class | XML package/class names match the code; the current hotel management class is named `HotelMagagementTest` |

## 9. Documents and Source Attribution

- [Testing documentation and automated test case list](../docs/testing/README.md)
- [Requirement Analysis](../docs/testing/Req-als.xlsx)
- [Test Scenarios](../docs/testing/Test-scenario.xlsx)
- [Test Cases](../docs/testing/Test-case.xlsx)
- [Original website repository — khusi2707/Hotel_booking_system](https://github.com/khusi2707/Hotel_booking_system)

When publishing the project on GitHub, retain the original website attribution, source files and testing documents. The `target/` directory and generated reports/screenshots do not need to be included in the source repository.
