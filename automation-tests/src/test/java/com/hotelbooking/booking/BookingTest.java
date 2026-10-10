package com.hotelbooking.booking;

import com.hotelbooking.base.BaseJavaTest;
import com.hotelbooking.pages.BookingPage;
import com.hotelbooking.pages.HomePage;
import com.hotelbooking.pages.HotelDetailPage;
import com.hotelbooking.pages.LoginPage;
import com.hotelbooking.pages.MyBookingsPage;
import com.hotelbooking.pages.PaymentPage;
import com.hotelbooking.utils.ConfigReader;
import com.hotelbooking.utils.WaitUtil;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;

public class BookingTest extends BaseJavaTest {
    private String bookingId;
    private String checkIn;
    private String checkOut;

    private BookingPage loginAndOpenBookingPage() {
        bookingId = null;
        driver.get(ConfigReader.get("baseUrl") + "/login");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginCustomer(
                ConfigReader.get("customerEmail"), ConfigReader.get("customerPassword"));
        WaitUtil.waitForUrlContains(driver, "/hotels");

        HomePage homePage = new HomePage(driver);
        Assert.assertTrue(homePage.isCustomerLogin(), "Customer login failed");
        homePage.openHotelByName(ConfigReader.get("bookingHotel"));

        HotelDetailPage hotelPage = new HotelDetailPage(driver);
        Assert.assertTrue(
                hotelPage.isRoomAvailable(ConfigReader.get("bookingRoom")),
                "Room is not available");
        hotelPage.bookRoom(ConfigReader.get("bookingRoom"));
        return new BookingPage(driver);
    }

    private MyBookingsPage submitBookingAndOpenMyBookings(
            BookingPage bookingPage, int daysFromToday, int nights) {
        LocalDate checkInDate = LocalDate.now().plusDays(daysFromToday);
        checkIn = checkInDate.toString();
        checkOut = checkInDate.plusDays(nights).toString();

        bookingPage.fillBooking(checkIn, checkOut, "1", "0");
        Assert.assertEquals(bookingPage.getCheckInValue(), checkIn, "Check-in is incorrect");
        Assert.assertEquals(bookingPage.getCheckOutValue(), checkOut, "Check-out is incorrect");
        bookingPage.confirmBooking();

        WaitUtil.waitForUrlContains(driver, "/payment/");
        PaymentPage paymentPage = new PaymentPage(driver);
        bookingId = paymentPage.getBookingId();
        paymentPage.waitUntilReady();

        new HomePage(driver).openMyBookings();
        WaitUtil.waitForUrlContains(driver, "/my-bookings");
        return new MyBookingsPage(driver, bookingId);
    }

    @Test
    public void TC_BOOKING_UI_001_bookingSuccessfully() {
        BookingPage bookingPage = loginAndOpenBookingPage();
        MyBookingsPage bookings = submitBookingAndOpenMyBookings(bookingPage, 10, 1);

        Assert.assertTrue(
                bookings.isBookingDisplayed(checkIn, checkOut), "Valid booking was not created");
        Assert.assertEquals(
                bookings.getBookingStatus(checkIn, checkOut).toLowerCase(),
                "confirmed",
                "Booking status is incorrect");
    }

    @Test
    public void TC_BOOKING_UI_002_amountForOneNight() {
        BookingPage bookingPage = loginAndOpenBookingPage();
        double expectedAmount = bookingPage.getPricePerNight();
        MyBookingsPage bookings = submitBookingAndOpenMyBookings(bookingPage, 20, 1);

        Assert.assertEquals(
                bookings.getBookingAmount(checkIn, checkOut),
                expectedAmount,
                0.01,
                "Amount for 1 night is incorrect");
    }

    @Test
    public void TC_BOOKING_UI_003_amountForMultipleNights() {
        BookingPage bookingPage = loginAndOpenBookingPage();
        int nights = 5;
        double expectedAmount = bookingPage.getPricePerNight() * nights;
        MyBookingsPage bookings = submitBookingAndOpenMyBookings(bookingPage, 30, nights);

        Assert.assertEquals(
                bookings.getBookingAmount(checkIn, checkOut),
                expectedAmount,
                0.01,
                "Amount for multiple nights is incorrect");
    }

    @Test
    public void TC_BOOKING_UI_005_invalidDate() {
        BookingPage bookingPage = loginAndOpenBookingPage();
        LocalDate checkInDate = LocalDate.now().plusDays(40);

        bookingPage.enterCheckIn(checkInDate.toString());
        bookingPage.enterCheckOut(checkInDate.minusDays(1).toString());

        Assert.assertEquals(
                bookingPage.getCheckoutMinDate(),
                checkInDate.toString(),
                "Checkout min date is incorrect");
        Assert.assertFalse(
                bookingPage.isCheckoutDateValid(),
                "System incorrectly accepts checkout before check-in");
    }

    @Test
    public void TC_BOOKING_UI_007_roomAvailableAfterCancel() {
        BookingPage bookingPage = loginAndOpenBookingPage();
        MyBookingsPage bookings = submitBookingAndOpenMyBookings(bookingPage, 70, 1);
        Assert.assertTrue(bookings.isBookingDisplayed(checkIn, checkOut), "Booking setup failed");
        bookings.cancelBooking(checkIn, checkOut);
        Assert.assertTrue(
                bookings.waitUntilBookingCancelled(checkIn, checkOut),
                "Booking cancellation failed");

        HomePage homePage = new HomePage(driver);
        homePage.openHotels();
        homePage.openHotelByName(ConfigReader.get("bookingHotel"));
        HotelDetailPage hotelPage = new HotelDetailPage(driver);
        Assert.assertTrue(
                hotelPage.isRoomAvailable(ConfigReader.get("bookingRoom")),
                "Room did not become available after cancellation");
    }

    @Test
    public void TC_BOOKING_UI_009_bookingDisplayedInMyBookings() {
        BookingPage bookingPage = loginAndOpenBookingPage();
        MyBookingsPage bookings = submitBookingAndOpenMyBookings(bookingPage, 50, 2);

        Assert.assertTrue(
                bookings.isBookingDisplayed(checkIn, checkOut),
                "Booking is not displayed in My Bookings");
    }

    @Test
    public void TC_BOOKING_UI_012_cancelBookingSuccessfully() {
        BookingPage bookingPage = loginAndOpenBookingPage();
        MyBookingsPage bookings = submitBookingAndOpenMyBookings(bookingPage, 60, 1);
        Assert.assertTrue(bookings.isBookingDisplayed(checkIn, checkOut), "Booking setup failed");
        bookings.cancelBooking(checkIn, checkOut);

        Assert.assertTrue(
                bookings.waitUntilBookingCancelled(checkIn, checkOut),
                "Booking was not cancelled successfully");
    }

    @AfterMethod(alwaysRun = true)
    public void cleanUpBooking() {
        if (bookingId == null) {
            return;
        }
        try {
            new HomePage(driver).openMyBookings();
            WaitUtil.waitForUrlContains(driver, "/my-bookings");
            MyBookingsPage bookings = new MyBookingsPage(driver, bookingId);
            if ("confirmed".equalsIgnoreCase(bookings.getBookingStatus(checkIn, checkOut))) {
                bookings.cancelBooking(checkIn, checkOut);
            }
        } finally {
            bookingId = null;
        }
    }
}
