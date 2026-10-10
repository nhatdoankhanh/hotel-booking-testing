package com.hotelbooking.pages;

import com.hotelbooking.utils.WaitUtil;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class MyBookingsPage {
    private WebDriver driver;
    private String bookingId;

    public MyBookingsPage(WebDriver driver) {
        this(driver, null);
    }

    public MyBookingsPage(WebDriver driver, String bookingId) {
        this.driver = driver;
        this.bookingId = bookingId;
    }

    private String bookingXpath(String checkIn, String checkOut) {
        String xpath =
                "//div[contains(@class,'card')][.//p[contains(.,'"
                        + checkIn
                        + "') and contains(.,'"
                        + checkOut
                        + "')]]";
        if (bookingId != null) {
            xpath += "[.//span[normalize-space()='Booking #" + bookingId + "']]";
        }
        return xpath;
    }

    private WebElement booking(String checkIn, String checkOut) {
        return WaitUtil.waitForVisible(driver, By.xpath(bookingXpath(checkIn, checkOut)));
    }

    public boolean isBookingDisplayed(String checkIn, String checkOut) {
        try {
            return booking(checkIn, checkOut).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getBookingStatus(String checkIn, String checkOut) {
        return booking(checkIn, checkOut).findElement(By.cssSelector(".badge")).getText().trim();
    }

    public double getBookingAmount(String checkIn, String checkOut) {
        String text =
                booking(checkIn, checkOut)
                        .findElement(By.xpath(".//p[contains(.,'\u20B9')]"))
                        .getText();
        return Double.parseDouble(text.replaceAll("[^0-9.]", ""));
    }

    public void cancelBooking(String checkIn, String checkOut) {
        WebElement cancelButton =
                booking(checkIn, checkOut)
                        .findElement(By.xpath(".//button[normalize-space()='Cancel']"));
        WaitUtil.waitForClickable(driver, cancelButton).click();
        WaitUtil.waitForAlert(driver).accept();
        waitUntilBookingCancelled(checkIn, checkOut);
    }

    public boolean waitUntilBookingCancelled(String checkIn, String checkOut) {
        By status = By.xpath(bookingXpath(checkIn, checkOut) + "//span[contains(@class,'badge')]");
        return WaitUtil.waitForCondition(driver, ExpectedConditions.textToBe(status, "cancelled"));
    }
}
