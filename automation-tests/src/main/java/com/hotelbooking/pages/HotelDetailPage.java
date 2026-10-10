package com.hotelbooking.pages;

import com.hotelbooking.utils.WaitUtil;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HotelDetailPage {
    private WebDriver driver;

    public HotelDetailPage(WebDriver driver) {
        this.driver = driver;
    }

    private WebElement bookNowButton(String roomName) {
        By locator =
                By.xpath(
                        "//h3[normalize-space()='"
                                + roomName
                                + "']/ancestor::div[contains(@class,'card')][1]//button[normalize-space()='Book"
                                + " Now']");

        return WaitUtil.waitForClickable(driver, locator);
    }

    public String getRoomStatus(String roomName) {
        By statusLocator =
                By.xpath(
                        "//h3[normalize-space()='"
                                + roomName
                                + "']/ancestor::div[contains(@class,'card')][1]//span[contains(@class,'badge')]");

        return WaitUtil.waitForVisible(driver, statusLocator).getText().trim();
    }

    public boolean isRoomAvailable(String roomName) {
        String status = getRoomStatus(roomName);

        return status.equalsIgnoreCase("Available");
    }

    public void bookRoom(String roomName) {
        bookNowButton(roomName).click();
    }
}
