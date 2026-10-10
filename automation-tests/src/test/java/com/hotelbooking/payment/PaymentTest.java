package com.hotelbooking.payment;

import com.hotelbooking.base.BaseJavaTest;
import com.hotelbooking.pages.BookingPage;
import com.hotelbooking.pages.HomePage;
import com.hotelbooking.pages.HotelDetailPage;
import com.hotelbooking.pages.LoginPage;
import com.hotelbooking.pages.MyBookingsPage;
import com.hotelbooking.pages.PaymentPage;
import com.hotelbooking.pages.admin.AdminHomePape;
import com.hotelbooking.pages.admin.AdminPaymentRecordsPage;
import com.hotelbooking.utils.ConfigReader;
import com.hotelbooking.utils.WaitUtil;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;

public class PaymentTest extends BaseJavaTest {
    private String bookingId;
    private String checkIn;
    private String checkOut;
    private double expectedAmount;
    private boolean adminLoggedIn;

    private void loginCustomer() {
        driver.get(ConfigReader.get("baseUrl") + "/login");

        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginCustomer(
                ConfigReader.get("customerEmail"), ConfigReader.get("customerPassword"));

        WaitUtil.waitForUrlContains(driver, "/hotels");
        Assert.assertTrue(new HomePage(driver).isCustomerLogin(), "Customer login failed");
    }

    private PaymentPage loginAndOpenPaymentPage(int daysFromToday) {
        bookingId = null;
        adminLoggedIn = false;
        loginCustomer();

        HomePage homePage = new HomePage(driver);
        homePage.openHotelByName(ConfigReader.get("bookingHotel"));

        HotelDetailPage hotelPage = new HotelDetailPage(driver);
        Assert.assertTrue(
                hotelPage.isRoomAvailable(ConfigReader.get("bookingRoom")),
                "Room is not available");
        hotelPage.bookRoom(ConfigReader.get("bookingRoom"));

        BookingPage bookingPage = new BookingPage(driver);
        LocalDate checkInDate = LocalDate.now().plusDays(daysFromToday);
        checkIn = checkInDate.toString();
        checkOut = checkInDate.plusDays(1).toString();
        expectedAmount = bookingPage.getPricePerNight();

        bookingPage.fillBooking(checkIn, checkOut, "1", "0");
        bookingPage.confirmBooking();

        WaitUtil.waitForUrlContains(driver, "/payment/");
        PaymentPage paymentPage = new PaymentPage(driver);
        bookingId = paymentPage.getBookingId();
        paymentPage.waitUntilReady();

        return paymentPage;
    }

    @Test
    public void TC_PAYMENT_UI_001_paymentSuccessfully() {
        PaymentPage paymentPage = loginAndOpenPaymentPage(80);

        paymentPage.selectPaymentMethod("online");
        paymentPage.pay();

        Assert.assertTrue(
                paymentPage.isPaymentSuccessful(), "Payment Successful was not displayed");
        Assert.assertEquals(
                paymentPage.getAmountPaid(), expectedAmount, 0.01, "Amount Paid is incorrect");
    }

    @Test
    public void TC_PAYMENT_UI_002_successfulPaymentDisplaysCorrectBookingId() {
        PaymentPage paymentPage = loginAndOpenPaymentPage(90);

        paymentPage.selectPaymentMethod("online");
        paymentPage.pay();

        Assert.assertTrue(
                paymentPage.isPaymentSuccessful(), "Payment Successful was not displayed");
        Assert.assertEquals(
                paymentPage.getSuccessfulBookingId(),
                bookingId,
                "Payment Successful shows an incorrect Booking ID");
    }

    @Test
    public void TC_PAYMENT_UI_003_paymentDisplayedInAdminPaymentRecords() {
        PaymentPage paymentPage = loginAndOpenPaymentPage(100);

        paymentPage.selectPaymentMethod("online");
        paymentPage.pay();
        Assert.assertTrue(
                paymentPage.isPaymentSuccessful(), "Payment Successful was not displayed");

        HomePage homePage = new HomePage(driver);
        homePage.logout();
        WaitUtil.waitForUrlContains(driver, "/login");

        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAdmin(ConfigReader.get("adminEmail"), ConfigReader.get("adminPassword"));
        WaitUtil.waitForUrlContains(driver, "/admin/dashboard");
        adminLoggedIn = true;
        Assert.assertTrue(new AdminHomePape(driver).isAdminLogin(), "Admin login failed");

        AdminPaymentRecordsPage records = new AdminPaymentRecordsPage(driver);
        records.open();

        Assert.assertTrue(
                records.isPaymentDisplayed(bookingId),
                "Payment is not displayed in Admin Payment Records");
        Assert.assertEquals(
                records.getAmount(bookingId),
                expectedAmount,
                0.01,
                "Admin payment amount is incorrect");
        Assert.assertEquals(
                records.getStatus(bookingId).toLowerCase(),
                "completed",
                "Admin payment status is incorrect");
    }

    // Huy booking vua tao qua UI de phong san sang cho testcase tiep theo.
    @AfterMethod(alwaysRun = true)
    public void cleanUpBooking() {
        if (bookingId == null) {
            return;
        }

        try {
            if (adminLoggedIn) {
                new HomePage(driver).logout();
                WaitUtil.waitForUrlContains(driver, "/login");
                loginCustomer();
            }

            new HomePage(driver).openMyBookings();
            WaitUtil.waitForUrlContains(driver, "/my-bookings");

            MyBookingsPage bookings = new MyBookingsPage(driver, bookingId);
            bookings.cancelBooking(checkIn, checkOut);
        } finally {
            bookingId = null;
        }
    }
}
