package com.hotelbooking.pages;

import com.hotelbooking.utils.WaitUtil;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

import java.net.URI;

public class PaymentPage {
    private WebDriver driver;

    private By payButton = By.xpath("//button[starts-with(normalize-space(),'Pay ')]");
    private By successHeading = By.xpath("//h2[normalize-space()='Payment Successful!']");

    public PaymentPage(WebDriver driver) {
        this.driver = driver;
    }

    public void waitUntilReady() {
        WaitUtil.waitForClickable(driver, payButton);
    }

    // Lay Booking ID tu URL dang hien thi: /payment/{bookingId}.
    public String getBookingId() {
        String path = URI.create(driver.getCurrentUrl()).getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }

    public void selectPaymentMethod(String method) {
        WaitUtil.waitForClickable(
                        driver, By.cssSelector("input[name='pm'][value='" + method + "']"))
                .click();
    }

    public void pay() {
        WaitUtil.waitForClickable(driver, payButton).click();
    }

    public boolean isPaymentSuccessful() {
        try {
            return WaitUtil.waitForVisible(driver, successHeading).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getSuccessfulBookingId() {
        String text =
                WaitUtil.waitForVisible(
                                driver, By.xpath("//p[strong[normalize-space()='Booking ID:']]"))
                        .getText();

        return text.replace("Booking ID:", "").replace("#", "").trim();
    }

    public double getAmountPaid() {
        String text =
                WaitUtil.waitForVisible(
                                driver, By.xpath("//p[strong[normalize-space()='Amount Paid:']]"))
                        .getText();

        return Double.parseDouble(text.replaceAll("[^0-9.]", ""));
    }

    public void viewMyBookings() {
        WaitUtil.waitForClickable(
                        driver, By.xpath("//button[normalize-space()='View My Bookings']"))
                .click();
        WaitUtil.waitForUrlContains(driver, "/my-bookings");
    }
}
