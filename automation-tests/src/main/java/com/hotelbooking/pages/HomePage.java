package com.hotelbooking.pages;

import com.hotelbooking.utils.WaitUtil;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class HomePage {
    private WebDriver driver;

    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    private WebElement myBookingsButton() {
        return WaitUtil.waitForClickable(driver, By.xpath("//a[@href='/my-bookings']"));
    }

    private WebElement logoutButton() {
        return WaitUtil.waitForClickable(driver, By.xpath("//button[contains(text(), 'Logout')]"));
    }

    private WebElement hotelsButton() {
        return WaitUtil.waitForClickable(driver, By.xpath("//a[@href='/hotels']"));
    }

    private WebElement searchCityInput() {
        return WaitUtil.waitForVisible(
                driver,
                By.cssSelector("input[placeholder='Search by city (e.g. Mumbai, Delhi...)']"));
    }

    private WebElement searchButton() {
        return WaitUtil.waitForClickable(
                driver, By.xpath("//form//button[@type='submit' and normalize-space()='Search']"));
    }

    private By hotelNames() {
        return By.xpath("//div[contains(@class,'grid-3')]/div//h3");
    }

    private By hotelCities() {
        return By.xpath("//div[contains(@class,'grid-3')]/div/div/p[1]");
    }

    public boolean isCustomerLogin() {
        return logoutButton().isDisplayed();
    }

    public void logout() {
        logoutButton().click();
    }

    public void searchHotelByCity(String city) {
        List<WebElement> oldHotels = driver.findElements(hotelNames());
        searchCityInput().clear();
        searchCityInput().sendKeys(city);
        searchButton().click();
        if (!oldHotels.isEmpty()) {
            WaitUtil.waitForCondition(driver, ExpectedConditions.stalenessOf(oldHotels.get(0)));
        }
        WaitUtil.waitForCondition(
                driver,
                webDriver ->
                        webDriver.findElements(By.cssSelector(".spinner")).isEmpty()
                                && (!webDriver.findElements(hotelNames()).isEmpty()
                                        || !webDriver
                                                .findElements(By.cssSelector(".empty-state"))
                                                .isEmpty()));
    }

    public boolean isHotelListDisplayed() {
        try {
            WaitUtil.waitForVisible(driver, hotelNames());

            List<WebElement> hotels = driver.findElements(hotelNames());

            return !hotels.isEmpty();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public int getHotelCount() {
        WaitUtil.waitForVisible(driver, hotelNames());

        return driver.findElements(hotelNames()).size();
    }

    public boolean areDisplayedCitiesContaining(String keyword) {
        try {
            WaitUtil.waitForVisible(driver, hotelCities());

            List<WebElement> cities = driver.findElements(hotelCities());

            if (cities.isEmpty()) {
                return false;
            }

            for (WebElement city : cities) {
                String actualCity = city.getText().replace("📍", "").trim().toLowerCase();

                if (!actualCity.contains(keyword.toLowerCase())) {
                    return false;
                }
            }

            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void openHotelByName(String hotelName) {
        By hotelCard =
                By.xpath("//h3[normalize-space()='" + hotelName + "']/parent::div/parent::div");

        WaitUtil.waitForClickable(driver, hotelCard).click();
    }

    public void openMyBookings() {
        myBookingsButton().click();
    }

    public void openHotels() {
        hotelsButton().click();
        WaitUtil.waitForUrlContains(driver, "/hotels");
    }

    public String getHotelCity(String hotelName) {
        String text =
                WaitUtil.waitForVisible(
                                driver,
                                By.xpath(
                                        "//h3[normalize-space()='"
                                                + hotelName
                                                + "']/following-sibling::p[1]"))
                        .getText();
        return text.replace("\uD83D\uDCCD", "").trim();
    }

    public void viewRooms(String hotelName) {
        WaitUtil.waitForClickable(
                        driver,
                        By.xpath(
                                "//h3[normalize-space()='"
                                        + hotelName
                                        + "']/parent::div//span[contains(normalize-space(),'View"
                                        + " Rooms')]"))
                .click();
    }
}
