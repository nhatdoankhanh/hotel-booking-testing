# Testing Documentation

This directory contains three documents I created while analyzing requirements and designing tests for Hotel Booking System: **Requirement Analysis, Test Scenarios and Test Cases**. Together with the automation suite, they describe the process from understanding requirements to designing scenarios and executing UI tests.

## Document Overview

### Requirement Analysis

[Req-als.xlsx](Req-als.xlsx) analyzes the website's requirements and provides a basis for identifying the features and behaviors to test. This is the starting point for understanding the system and defining the testing scope.

### Test Scenarios

[Test-scenario.xlsx](Test-scenario.xlsx) organizes the scenarios to test based on the requirement analysis. Scenarios describe situations or user workflows at a high level and guide the design of detailed test cases.

### Test Cases

[Test-case.xlsx](Test-case.xlsx) contains detailed test cases for executing tests and checking results. It turns scenarios into individual checks that can be performed on the website.

### API Testing with Postman

The [API testing collections](../../api-tests/README.md) support manual API checks across authentication, authorization, hotels, rooms, bookings, payments, customers and dashboard data. The collections contain 63 saved requests with configured URLs. Six previously incomplete requests were completed from the test case document and backend source, and three unnamed empty requests were removed. They complement the detailed test cases and do not currently contain automated response assertions.

### UI Automation Testing

The [automation suite](../../automation-tests/README.md) implements selected scenarios using Java, Selenium WebDriver and TestNG. It currently contains 25 UI test cases covering Authentication, Hotel Search, Hotel Management, Booking, Payment and two E2E flows.

The workflow is **Analyze Requirements → Define Test Scenarios → Write Test Cases → Automate Selected Cases → Execute and Review Results**.

## Document Files

| File | Description |
| --- | --- |
| [Req-als.xlsx](Req-als.xlsx) | Requirement analysis |
| [Test-scenario.xlsx](Test-scenario.xlsx) | Test scenarios |
| [Test-case.xlsx](Test-case.xlsx) | Detailed test cases |

For environment setup, test data, suite execution and reports, see the [Automation Testing README](../../automation-tests/README.md).

## Automated Test Cases

This list matches the TestNG methods in the current source code. IDs are preserved as defined in the project, including gaps in numbering.

| Group | Test Case | Description |
| --- | --- | --- |
| Authentication | TC_AUTH_UI_001 | Successful registration |
| Authentication | TC_AUTH_UI_002 | Registration with a duplicate email |
| Authentication | TC_AUTH_UI_004 | Customer login |
| Authentication | TC_AUTH_UI_005 | Admin login |
| Authentication | TC_AUTH_UI_006 | Login with an incorrect password |
| Authentication | TC_AUTH_UI_007 | Customer session persists after refresh |
| Hotel Search | TC_HOTEL_UI_001 | Display the hotel list |
| Hotel Search | TC_HOTEL_UI_003 | Search by city |
| Hotel Search | TC_HOTEL_UI_004 | Case-insensitive city search |
| Hotel Search | TC_HOTEL_UI_005 | Partial city search |
| Hotel Management | TC_HOTEL_UI_006 | Add a hotel |
| Hotel Management | TC_HOTEL_UI_007 | Update a hotel |
| Hotel Management | TC_HOTEL_UI_008 | Delete a hotel |
| Booking | TC_BOOKING_UI_001 | Successful room booking |
| Booking | TC_BOOKING_UI_002 | Calculate the amount for one night |
| Booking | TC_BOOKING_UI_003 | Calculate the amount for multiple nights |
| Booking | TC_BOOKING_UI_005 | Invalid booking dates |
| Booking | TC_BOOKING_UI_007 | Room becomes available after cancellation |
| Booking | TC_BOOKING_UI_009 | Booking appears in My Bookings |
| Booking | TC_BOOKING_UI_012 | Successful booking cancellation |
| Payment | TC_PAYMENT_UI_001 | Successful payment |
| Payment | TC_PAYMENT_UI_002 | Payment Successful displays the correct Booking ID |
| Payment | TC_PAYMENT_UI_003 | Payment appears in Admin Payment Records |
| E2E | TC_E2E_UI_001 | Search → book → pay → View My Bookings |
| E2E | TC_E2E_UI_002 | Cancel → room available → reopen booking form |

**Total: 25 UI test cases.** This list describes the existing automation code and does not imply that every test case in the Excel documents has been automated.

## Original Website

The website is based on [khusi2707/Hotel_booking_system](https://github.com/khusi2707/Hotel_booking_system). The automation tests and QA documents are additions to the original website project.
