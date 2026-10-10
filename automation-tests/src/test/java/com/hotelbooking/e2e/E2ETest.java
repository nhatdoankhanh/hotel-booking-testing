package com.hotelbooking.e2e;

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

import java.net.URI;
import java.time.LocalDate;

public class E2ETest extends BaseJavaTest {
    private String bookingId;
    private String checkIn;
    private String checkOut;

    private HomePage loginCustomerAndBrowseHotels() {
        bookingId = null;
        driver.get(ConfigReader.get("baseUrl") + "/login");

        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginCustomer(
                ConfigReader.get("customerEmail"), ConfigReader.get("customerPassword"));
        WaitUtil.waitForUrlContains(driver, "/hotels");

        HomePage homePage = new HomePage(driver);
        Assert.assertTrue(homePage.isCustomerLogin(), "Customer login failed");
        Assert.assertTrue(homePage.isHotelListDisplayed(), "Hotel list is not displayed");
        return homePage;
    }

    private HotelDetailPage viewConfiguredRoom(HomePage homePage) {
        homePage.viewRooms(ConfigReader.get("bookingHotel"));

        HotelDetailPage hotelPage = new HotelDetailPage(driver);
        Assert.assertTrue(
                hotelPage.isRoomAvailable(ConfigReader.get("bookingRoom")),
                "Room is not available");
        return hotelPage;
    }

    private PaymentPage confirmBooking(BookingPage bookingPage, int daysFromToday) {
        LocalDate checkInDate = LocalDate.now().plusDays(daysFromToday);
        checkIn = checkInDate.toString();
        checkOut = checkInDate.plusDays(1).toString();

        bookingPage.fillBooking(checkIn, checkOut, "1", "0");
        Assert.assertEquals(bookingPage.getCheckInValue(), checkIn, "Check-in is incorrect");
        Assert.assertEquals(bookingPage.getCheckOutValue(), checkOut, "Check-out is incorrect");
        bookingPage.confirmBooking();

        WaitUtil.waitForUrlContains(driver, "/payment/");
        PaymentPage paymentPage = new PaymentPage(driver);
        bookingId = paymentPage.getBookingId();
        paymentPage.waitUntilReady();
        return paymentPage;
    }

    @Test
    public void TC_E2E_UI_001_searchBookPayAndViewMyBookings() {
        HomePage homePage = loginCustomerAndBrowseHotels();

        String city = homePage.getHotelCity(ConfigReader.get("bookingHotel"));
        Assert.assertFalse(city.isEmpty(), "Hotel city is empty");
        homePage.searchHotelByCity(city);
        Assert.assertTrue(
                homePage.areDisplayedCitiesContaining(city),
                "Search result contains an incorrect city");

        HotelDetailPage hotelPage = viewConfiguredRoom(homePage);
        hotelPage.bookRoom(ConfigReader.get("bookingRoom"));

        BookingPage bookingPage = new BookingPage(driver);
        double expectedAmount = bookingPage.getPricePerNight();
        PaymentPage paymentPage = confirmBooking(bookingPage, 110);

        paymentPage.selectPaymentMethod("online");
        paymentPage.pay();

        Assert.assertTrue(
                paymentPage.isPaymentSuccessful(), "Payment Successful was not displayed");
        Assert.assertEquals(
                paymentPage.getSuccessfulBookingId(),
                bookingId,
                "Payment shows an incorrect Booking ID");
        Assert.assertEquals(
                paymentPage.getAmountPaid(), expectedAmount, 0.01, "Amount Paid is incorrect");

        paymentPage.viewMyBookings();

        MyBookingsPage bookings = new MyBookingsPage(driver, bookingId);
        Assert.assertTrue(
                bookings.isBookingDisplayed(checkIn, checkOut),
                "Paid booking is not displayed in My Bookings");
        Assert.assertEquals(
                bookings.getBookingStatus(checkIn, checkOut).toLowerCase(),
                "confirmed",
                "Booking status is incorrect");
        Assert.assertEquals(
                bookings.getBookingAmount(checkIn, checkOut),
                expectedAmount,
                0.01,
                "My Bookings amount is incorrect");
    }

    @Test
    public void TC_E2E_UI_002_cancelBookingAndBookRoomAgain() {
        HomePage homePage = loginCustomerAndBrowseHotels();
        HotelDetailPage hotelPage = viewConfiguredRoom(homePage);
        hotelPage.bookRoom(ConfigReader.get("bookingRoom"));
        WaitUtil.waitForUrlContains(driver, "/book/");

        String originalBookingPath = URI.create(driver.getCurrentUrl()).getPath();
        BookingPage bookingPage = new BookingPage(driver);
        confirmBooking(bookingPage, 120);

        homePage.openMyBookings();
        WaitUtil.waitForUrlContains(driver, "/my-bookings");

        MyBookingsPage bookings = new MyBookingsPage(driver, bookingId);
        Assert.assertTrue(
                bookings.isBookingDisplayed(checkIn, checkOut),
                "Booking is not displayed in My Bookings");
        bookings.cancelBooking(checkIn, checkOut);
        Assert.assertEquals(
                bookings.getBookingStatus(checkIn, checkOut).toLowerCase(),
                "cancelled",
                "Booking was not cancelled");
        bookingId = null;

        homePage.openHotels();
        Assert.assertTrue(homePage.isHotelListDisplayed(), "Hotel list is not displayed");
        homePage.viewRooms(ConfigReader.get("bookingHotel"));

        hotelPage = new HotelDetailPage(driver);
        Assert.assertTrue(
                hotelPage.isRoomAvailable(ConfigReader.get("bookingRoom")),
                "Room did not become available after cancellation");
        hotelPage.bookRoom(ConfigReader.get("bookingRoom"));
        WaitUtil.waitForUrlContains(driver, "/book/");

        Assert.assertEquals(
                URI.create(driver.getCurrentUrl()).getPath(),
                originalBookingPath,
                "Book Now opened a different room");
        Assert.assertTrue(
                new BookingPage(driver).getPricePerNight() > 0,
                "Booking form was not displayed after clicking Book Now again");
    }

    // Huy booking cua testcase qua UI de giai phong phong.
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
