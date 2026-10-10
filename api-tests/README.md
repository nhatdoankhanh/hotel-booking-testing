# API Testing with Postman

This directory contains my Postman collections for manual API testing of Hotel Booking System. It complements the [requirement analysis and test design documents](../docs/testing/README.md) and the [Selenium UI automation suite](../automation-tests/README.md).

Original website: [khusi2707/Hotel_booking_system](https://github.com/khusi2707/Hotel_booking_system). The API collections and QA documentation are additions to the original application.

## Collection Overview

| Collection | Saved Requests | Requests with Empty URLs | Intended Coverage |
| --- | ---: | ---: | --- |
| [Authentication](collections/Authentication.postman_collection.json) | 11 | 3 | Registration, duplicate email, Customer/Admin login, wrong password and token scenarios |
| [Authorization](collections/Authorization.postman_collection.json) | 4 | 0 | Customer access to hotel, room, user and payment endpoints |
| [Hotel Management](<collections/Hotel Management.postman_collection.json>) | 9 | 1 | Hotel listing, city filters, hotel creation/update and one payment request |
| [Room Management](<collections/Room Management.postman_collection.json>) | 10 | 0 | Room listing, availability, creation, update and deletion |
| [Booking Management](<collections/Booking Management.postman_collection.json>) | 15 | 3 | Booking creation, date/ID/availability scenarios, amounts, booking history and cancellation draft |
| [Payment Management](<collections/Payment Management.postman_collection.json>) | 6 | 1 | Payment creation, transaction, missing booking, amount and duplicate-payment scenarios |
| [Customer Management](<collections/Customer Management.postman_collection.json>) | 6 | 1 | User listing, access control and deletion |
| [Admin DashBoard](<collections/Admin DashBoard.postman_collection.json>) | 5 | 0 | Hotel, booking, user and payment data requests used to inspect dashboard information |
| **Total** | **66** | **9** | **8 collections** |

The export contains **57 requests with configured URLs** and **9 incomplete requests**. A configured URL alone does not mean the request is ready for the current database or has passed testing. Names and collection placement are retained from the supplied exports, including draft requests and the payment request in Hotel Management.

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

## Incomplete Requests

The following requests have empty URLs in the original exports. Complete their method, URL, body and authorization before executing them; exclude them from collection runs until then.

| Collection | Request |
| --- | --- |
| Authentication | Three requests named `New Request` |
| Booking Management | `POST Booking - Invalid Date` |
| Booking Management | `GET Booking - Other Customer` |
| Booking Management | `PUT Booking - Cancel Booking` |
| Customer Management | `DELETE - User ID` |
| Hotel Management | `PUT Hotel - Not exists` |
| Payment Management | `GET All Payment` |

Some draft names indicate POST, PUT or DELETE while their saved method is GET. The original method is preserved here; verify it against the API documentation when completing the request.

## Publication Changes

- Original exports in Downloads were left unchanged.
- Registration/login credentials in request bodies were replaced with environment variable references.
- Environment credentials and tokens were cleared; local setup variables were added.
- Literal JWT values, if present, were replaced with a placeholder. Saved response bodies, headers and cookies are not included as execution evidence.
- Request names, configured endpoint URLs, test data IDs/dates and existing token-storage scripts were preserved.

## Related Documents

- [Requirement Analysis](../docs/testing/Req-als.xlsx)
- [Test Scenarios](../docs/testing/Test-scenario.xlsx)
- [Test Cases](../docs/testing/Test-case.xlsx)
- [UI Automation Testing](../automation-tests/README.md)
- [Original Website Repository](https://github.com/khusi2707/Hotel_booking_system)
