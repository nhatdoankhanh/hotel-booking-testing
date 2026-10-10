# API Testing with Postman

This directory contains my Postman collections for manual API testing of Hotel Booking System. It complements the [requirement analysis and test design documents](../docs/testing/README.md) and the [Selenium UI automation suite](../automation-tests/README.md).

Original website: [khusi2707/Hotel_booking_system](https://github.com/khusi2707/Hotel_booking_system). The API collections and QA documentation are additions to the original application.

## Collection Overview

| Collection | Saved Requests | Requests with Empty URLs | Intended Coverage |
| --- | ---: | ---: | --- |
| [Authentication](collections/Authentication.postman_collection.json) | 8 | 0 | Registration, duplicate email, Customer/Admin login, wrong password and token scenarios |
| [Authorization](collections/Authorization.postman_collection.json) | 4 | 0 | Customer access to hotel, room, user and payment endpoints |
| [Hotel Management](<collections/Hotel Management.postman_collection.json>) | 9 | 0 | Hotel listing, city filters, hotel creation/update and one payment request |
| [Room Management](<collections/Room Management.postman_collection.json>) | 10 | 0 | Room listing, availability, creation, update and deletion |
| [Booking Management](<collections/Booking Management.postman_collection.json>) | 15 | 0 | Booking creation, date/ID/availability scenarios, amounts, booking history and cancellation |
| [Payment Management](<collections/Payment Management.postman_collection.json>) | 6 | 0 | Payment creation, transaction, missing booking, amount and duplicate-payment scenarios |
| [Customer Management](<collections/Customer Management.postman_collection.json>) | 6 | 0 | User listing, access control and deletion |
| [Admin DashBoard](<collections/Admin DashBoard.postman_collection.json>) | 5 | 0 | Hotel, booking, user and payment data requests used to inspect dashboard information |
| **Total** | **63** | **0** | **8 collections** |

All **63 saved requests have configured URLs**. Three unnamed requests with empty URLs were removed from Authentication. Six named requests were completed using the test case document and backend controller mappings. A configured URL does not mean the request has passed testing; prepare the required tokens, IDs and database state before execution. The payment request originally exported under Hotel Management remains in that collection.

## Current Testing Status

These collections are primarily intended for manual execution and response inspection. Two login requests contain post-response scripts that store access tokens. The supplied collections contain no `pm.test(...)` assertions, so they do not currently provide automated pass/fail validation of API behavior.

The API requests were not executed as part of adding these files to the repository. No API pass rate or execution report is claimed here. The recorded 25 passing tests in the automation README refer to the separate Selenium UI suite.

## Directory Structure

```text
api-tests/
├── README.md
├── collections/
│   └── *.postman_collection.json
└── environments/
    └── LocalHost.postman_environment.json
```

## Import and Setup

1. Start the backend using the instructions in the [main README](../README.md#getting-started). The default API URL is `http://localhost:8000`.
2. In Postman, select **Import** and import all eight JSON files from `collections/`.
3. Import [LocalHost.postman_environment.json](environments/LocalHost.postman_environment.json).
4. Select **LocalHost** as the active environment.
5. Fill in the local account variables below. Existing Customer and Admin accounts must be available in your test database.

| Variable | Purpose |
| --- | --- |
| `BaseURL` | Backend URL, default `http://localhost:8000` |
| `customer_email`, `customer_password` | Existing Customer account credentials |
| `admin_email`, `admin_password` | Existing Admin account credentials |
| `register_email`, `register_password`, `register_name` | Test account data for registration scenarios |
| `wrong_password` | Deliberately incorrect password for the failed-login scenario |
| `customer_access_token` | Populated by the Customer login response script |
| `admin_access_token` | Populated by the Admin login response script |
| `authorization_secret` | Legacy token variable referenced by several requests; set the token appropriate to the scenario |
| `token` | Legacy token variable referenced by the hotel substring-search request |
| `redacted_token` | Placeholder for any literal token removed during publication; fill only if a request needs it |
| `second_customer_access_token` | Token for a second Customer account used to verify booking privacy |
| `hotel_id`, `room_id` | Existing hotel and available room for the equal-date negative booking scenario |
| `booking_equal_date` | Same check-in/check-out date; keep it today or in the future |
| `booking_id` | Confirmed booking owned by the Customer used for cancellation |
| `user_id` | Existing test user targeted by the Customer-token deletion authorization check |

The environment is a template. Account credentials and access tokens are empty in the committed file. Set them locally in Postman and do not publish an environment export containing live credentials.

## Authentication and Token Reuse

Open **Authentication** and send the Customer and Admin login requests with valid local credentials. Their existing scripts read `access_token` from the response and store it in the active environment:

```javascript
const response = pm.response.json();
pm.environment.set("customer_access_token", response.access_token);
```

The Admin login request uses the same pattern with `admin_access_token`. Requests referencing those variables can then reuse the tokens. If login fails or the response does not contain `access_token`, fix the account/environment before sending protected requests. See [Postman's environment variable documentation](https://learning.postman.com/docs/use/send-requests/variables/environment-variables).

Some exported requests reference `authorization_secret` instead of a role-specific variable:

- **POST New Hotel - Valid data:** use a valid Admin token.
- **GET Booking - No Boking:** use the intended Customer token and an account with the required booking state.
- **GET User - Invalid Token:** use a deliberately invalid token.
- **GET User - Expired Token:** use an actually expired test token.

These scenarios require different values. Set the value before sending each relevant request. The exported environment alone cannot create an expired token.

## Running the Requests

1. Confirm that the backend is reachable and select LocalHost.
2. Perform the login requests to populate the required tokens.
3. Open the collection for the feature being tested.
4. Review each request's URL, HTTP method, authorization, body and prerequisite data before selecting **Send**.
5. Compare the response status, body and resulting application state with the expected behavior in your [test cases](../docs/testing/Test-case.xlsx).

Update fixed hotel, room, user and booking IDs to match your local database. Review booking dates and room availability as well. Keep deliberately invalid IDs/dates when testing negative scenarios. Registration and duplicate-email scenarios require consistent registration data; repeat runs may need a new email for the successful-registration case.

Requests that add, update, cancel or delete records affect the test database. Use dedicated test data and execute scenarios in an order that satisfies their prerequisites.

For a Collection Runner run, select only requests that have been completed and prepared for the current environment. The collections are not yet a complete sequential API automation suite.

## Completed Requests and Test Case References

Six requests were completed by comparing [Test-case.xlsx](../docs/testing/Test-case.xlsx) with the backend controllers. The worksheet is `Sheet2`.

| Collection | Request | Method and Endpoint | Test Case | Worksheet Row |
| --- | --- | --- | --- | ---: |
| Booking Management | POST Booking - Equal Check-In and Check-Out | `POST /bookings` | TC_BM_API_008 | 78 |
| Booking Management | GET Booking - Other Customer | `GET /bookings/my` | TC_BM_API_012 | 82 |
| Booking Management | PUT Booking - Cancel Booking | `PUT /bookings/{{booking_id}}/cancel` | TC_BM_API_014 | 84 |
| Customer Management | DELETE User - Customer Token | `DELETE /users/{{user_id}}` | TC_USER_API_005 | 110 |
| Hotel Management | PUT Hotel - Not exists | `PUT /hotels/9999` | TC_HM_API_009 | 38 |
| Payment Management | GET All Payment | `GET /payments` | TC_PAY_API_007 | 99 |

The three unnamed `New Request` entries with empty URLs in Authentication were deleted. Their original intent could not be recovered from the export. The configured `New Request` in Admin DashBoard was retained.

For the booking privacy request, obtain a token for a second Customer account using `POST /auth/login` and put it in `second_customer_access_token`. If reusing the existing Customer login request, temporarily enter Customer 2 credentials, copy the returned token into this variable, then restore Customer 1 credentials and log in again to restore `customer_access_token`. Customer 1 must already have bookings for the privacy comparison to be meaningful.

For the equal-date request, set valid `hotel_id` and `room_id` values and choose a `booking_equal_date` that is not in the past. Using the same date for check-in and check-out isolates the boundary condition in TC_BM_API_008.

For cancellation, choose an existing confirmed booking belonging to the current Customer. For hotel 9999, confirm that the ID is absent. The user-deletion request is intentionally sent with a Customer token to verify access denial.

Methods, routes, authorization roles and body fields were checked against the backend source. The backend was not running during this update, so these completed scenarios have not been executed against the API.

## Publication Changes

- Original exports in Downloads were left unchanged.
- Registration/login credentials in request bodies were replaced with environment variable references.
- Environment credentials and tokens were cleared; local setup variables were added.
- Literal JWT values, if present, were replaced with a placeholder. Saved response bodies, headers and cookies are not included as execution evidence.
- Existing configured requests and token-storage scripts were preserved. The six formerly incomplete requests were assigned methods, endpoints, authorization and required bodies based on their documented scenarios.

## Related Documents

- [Requirement Analysis](../docs/testing/Req-als.xlsx)
- [Test Scenarios](../docs/testing/Test-scenario.xlsx)
- [Test Cases](../docs/testing/Test-case.xlsx)
- [UI Automation Testing](../automation-tests/README.md)
- [Original Website Repository](https://github.com/khusi2707/Hotel_booking_system)
