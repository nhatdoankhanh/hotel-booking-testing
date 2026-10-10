package com.hotelbooking.pages;

import com.hotelbooking.utils.WaitUtil;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPage {
    protected WebDriver driver;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    private WebElement customerTab() {
        return WaitUtil.waitForClickable(
                driver, By.xpath("//button[contains(text(), 'Customer')]"));
    }

    private WebElement adminTab() {
        return WaitUtil.waitForClickable(driver, By.xpath("//button[contains(text(), 'Admin')]"));
    }

    private WebElement emailInput() {
        return WaitUtil.waitForVisible(driver, By.cssSelector("input[type='email']"));
    }

    private WebElement passwordInput() {
        return WaitUtil.waitForVisible(driver, By.cssSelector("input[type='password']"));
    }

    private WebElement signInAsCustomerButton() {
        return WaitUtil.waitForClickable(
                driver, By.xpath("//button[contains(text(), 'Sign In as Customer')]"));
    }

    private WebElement signInAsAdminButton() {
        return WaitUtil.waitForClickable(
                driver, By.xpath("//button[contains(text(), 'Sign In as Admin')]"));
    }

    public void selectAdminTab() {
        adminTab().click();
    }

    public void selectCustomerTab() {
        customerTab().click();
    }

    public void enterEmail(String email) {
        emailInput().clear();
        emailInput().sendKeys(email);
    }

    public void enterPassword(String password) {
        passwordInput().clear();
        passwordInput().sendKeys(password);
    }

    public void loginCustomer(String email, String password) {
        selectCustomerTab();
        enterEmail(email);
        enterPassword(password);
        signInAsCustomerButton().click();
    }

    public void loginAdmin(String email, String password) {
        selectAdminTab();
        enterEmail(email);
        enterPassword(password);
        signInAsAdminButton().click();
    }
}
