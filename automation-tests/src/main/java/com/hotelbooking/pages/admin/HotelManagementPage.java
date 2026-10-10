package com.hotelbooking.pages.admin;

import com.hotelbooking.utils.WaitUtil;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class HotelManagementPage {
    private WebDriver driver;

    public HotelManagementPage(WebDriver driver) {
        this.driver = driver;
    }

    private WebElement addHotelButton() {
        return WaitUtil.waitForClickable(
                driver, By.xpath("//button[normalize-space()='+ Add Hotel']"));
    }

    private WebElement hotelNameInput() {
        return WaitUtil.waitForVisible(
                driver,
                By.xpath(
                        "//div[contains(@class,'modal')]//label[contains(normalize-space(),'Hotel"
                            + " Name')]/following-sibling::input"));
    }

    private WebElement cityInput() {
        return WaitUtil.waitForVisible(
                driver,
                By.xpath(
                        "//div[contains(@class,'modal')]//label[contains(normalize-space(),'City')]/following-sibling::input"));
    }

    private WebElement avgRateInput() {
        return WaitUtil.waitForVisible(
                driver,
                By.xpath(
                        "//div[contains(@class,'modal')]//label[contains(normalize-space(),'Avg"
                            + " Rate/Night')]/following-sibling::input"));
    }

    private WebElement submitButton() {
        return WaitUtil.waitForClickable(
                driver, By.xpath("//div[contains(@class,'modal')]//button[@type='submit']"));
    }

    private By hotelNameLocator(String hotelName) {
        return By.xpath("//tbody/tr/td/strong[normalize-space()='" + hotelName + "']");
    }

    private WebElement editButton(String hotelName) {
        return WaitUtil.waitForClickable(
                driver,
                By.xpath(
                        "//tbody/tr[td/strong[normalize-space()='"
                                + hotelName
                                + "']]//button[normalize-space()='Edit']"));
    }

    private WebElement deleteButton(String hotelName) {
        return WaitUtil.waitForClickable(
                driver,
                By.xpath(
                        "//tbody/tr[td/strong[normalize-space()='"
                                + hotelName
                                + "']]//button[normalize-space()='Delete']"));
    }

    private void enterData(WebElement element, String value) {
        element.clear();

        if (value != null) {
            element.sendKeys(value);
        }
    }

    private void fillHotelForm(String hotelName, String city, String avgRate) {
        enterData(hotelNameInput(), hotelName);
        enterData(cityInput(), city);
        enterData(avgRateInput(), avgRate);
    }

    public void addHotel(String hotelName, String city, String avgRate) {
        addHotelButton().click();

        fillHotelForm(hotelName, city, avgRate);

        submitButton().click();
    }

    public boolean isHotelDisplayed(String hotelName) {
        try {
            return WaitUtil.waitForVisible(driver, hotelNameLocator(hotelName)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getHotelCity(String hotelName) {
        WebElement city =
                WaitUtil.waitForVisible(
                        driver,
                        By.xpath(
                                "//tbody/tr[td/strong[normalize-space()='"
                                        + hotelName
                                        + "']]/td[3]"));

        return city.getText();
    }

    public String getHotelRate(String hotelName) {
        WebElement rate =
                WaitUtil.waitForVisible(
                        driver,
                        By.xpath(
                                "//tbody/tr[td/strong[normalize-space()='"
                                        + hotelName
                                        + "']]/td[4]"));

        return rate.getText();
    }

    public void updateHotel(
            String currentHotelName, String newHotelName, String city, String avgRate) {
        editButton(currentHotelName).click();

        fillHotelForm(newHotelName, city, avgRate);
        submitButton().click();
    }

    public void deleteHotel(String hotelName) {
        deleteButton(hotelName).click();
        WaitUtil.waitForAlert(driver).accept();
    }

    public boolean waitUntilHotelRemoved(String hotelName) {
        List<WebElement> hotels = driver.findElements(hotelNameLocator(hotelName));

        if (hotels.isEmpty()) {
            return true;
        }

        return WaitUtil.waitForInvisible(driver, hotels.get(0));
    }
}
