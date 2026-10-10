package com.hotelbooking.pages.admin;

import com.hotelbooking.utils.WaitUtil;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AdminHomePape {
    private WebDriver driver;

    public AdminHomePape(WebDriver driver) {
        this.driver = driver;
    }

    private WebElement logoutButton() {
        return WaitUtil.waitForClickable(driver, By.xpath("//button[contains(text(), 'Logout')]"));
    }

    private WebElement hotelsButton() {
        return WaitUtil.waitForClickable(driver, By.cssSelector("a[href='/admin/hotels']"));
    }

    public boolean isAdminLogin() {
        return logoutButton().isDisplayed();
    }

    public void openHotelManagement() {
        hotelsButton().click();
    }
}
