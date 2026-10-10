package com.hotelbooking.pages;

import com.hotelbooking.utils.WaitUtil;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;

public class BookingPage {
    private WebDriver driver;

    public BookingPage(WebDriver driver) {
        this.driver = driver;
    }

    private WebElement input(String label) {
        return WaitUtil.waitForVisible(
                driver,
                By.xpath(
                        "//label[contains(normalize-space(),'"
                                + label
                                + "')]/following-sibling::input"));
    }

    private void setDate(WebElement input, String date) {
        LocalDate.parse(date);
        // Native setter + event de React cap nhat gia tri ngay.
        ((JavascriptExecutor) driver)
                .executeScript(
                        """
const setter = Object.getOwnPropertyDescriptor(HTMLInputElement.prototype, 'value').set;
setter.call(arguments[0], arguments[1]);
arguments[0].dispatchEvent(new Event('input', {bubbles: true}));
arguments[0].dispatchEvent(new Event('change', {bubbles: true}));
""",
                        input,
                        date);
        WaitUtil.waitForCondition(driver, ignored -> date.equals(input.getDomProperty("value")));
    }

    public void enterCheckIn(String date) {
        setDate(input("Check-In Date"), date);
    }

    public void enterCheckOut(String date) {
        setDate(input("Check-Out Date"), date);
    }

    public void enterAdults(String adults) {
        WebElement input = input("No. of Adults");
        input.clear();
        input.sendKeys(adults);
    }

    public void enterChildren(String children) {
        WebElement input = input("No. of Children");
        input.clear();
        input.sendKeys(children);
    }

    public void fillBooking(String checkIn, String checkOut, String adults, String children) {
        enterCheckIn(checkIn);
        enterCheckOut(checkOut);
        enterAdults(adults);
        enterChildren(children);
    }

    public void confirmBooking() {
        WaitUtil.waitForClickable(
                        driver,
                        By.xpath(
                                "//button[@type='submit' and contains(normalize-space(),'Confirm"
                                        + " Booking')]"))
                .click();
    }

    public double getPricePerNight() {
        String text =
                WaitUtil.waitForVisible(
                                driver,
                                By.xpath(
                                        "//h3[normalize-space()='Room & Hotel"
                                            + " Details']/following::p[contains(.,'/night')][1]"))
                        .getText();
        return Double.parseDouble(text.replaceAll("[^0-9.]", ""));
    }

    public boolean isCheckoutDateValid() {
        return (Boolean)
                ((JavascriptExecutor) driver)
                        .executeScript(
                                "return arguments[0].checkValidity();", input("Check-Out Date"));
    }

    public String getCheckoutMinDate() {
        return input("Check-Out Date").getDomAttribute("min");
    }

    public String getCheckInValue() {
        return input("Check-In Date").getDomProperty("value");
    }

    public String getCheckOutValue() {
        return input("Check-Out Date").getDomProperty("value");
    }
}
