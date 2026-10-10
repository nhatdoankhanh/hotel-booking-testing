package com.hotelbooking.pages;

import com.hotelbooking.utils.WaitUtil;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class RegisterPage {
    private WebDriver driver;

    public RegisterPage(WebDriver driver) {
        this.driver = driver;
    }

    private WebElement fullNameInput() {
        return WaitUtil.waitForVisible(
                driver, By.xpath("//input[@type='text' and @placeholder='John Doe']"));
    }

    private WebElement emailAddressInput() {
        return WaitUtil.waitForVisible(
                driver, By.xpath("//input[@type='email' and @placeholder='john@example.com']"));
    }

    private WebElement passwordInput() {
        return WaitUtil.waitForVisible(
                driver, By.xpath("//input[@type='password' and @placeholder='Min 8 characters']"));
    }

    private WebElement createAccountButton() {
        return WaitUtil.waitForClickable(
                driver, By.xpath("//button[@type='submit' and text()='Create Account']"));
    }

    private WebElement emailDuplicateError() {
        return WaitUtil.waitForVisible(
                driver,
                By.xpath(
                        "//div[contains(@class, 'alert-error') and text()='Email already"
                            + " registered']"));
    }

    public void enterFullName(String fullName) {
        fullNameInput().clear();
        fullNameInput().sendKeys(fullName);
    }

    public void enterEmailAddress(String email) {
        emailAddressInput().clear();
        emailAddressInput().sendKeys(email);
    }

    public void enterPassword(String password) {
        passwordInput().clear();
        passwordInput().sendKeys(password);
    }

    public void clickCreateAccount() {
        createAccountButton().click();
    }

    public void register(String fullName, String emailAddress, String password) {
        enterFullName(fullName);
        enterEmailAddress(emailAddress);
        enterPassword(password);
        clickCreateAccount();
    }

    public boolean isDuplicateEmail() {
        return emailDuplicateError().isDisplayed();
    }
}
