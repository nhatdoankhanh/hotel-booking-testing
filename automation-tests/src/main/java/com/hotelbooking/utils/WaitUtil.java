package com.hotelbooking.utils;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Function;

public class WaitUtil {
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    public static WebElement waitForClickable(WebDriver driver, WebElement element) {
        return new WebDriverWait(driver, TIMEOUT)
                .until(ExpectedConditions.elementToBeClickable(element));
    }

    public static WebElement waitForClickable(WebDriver driver, By locator) {
        return new WebDriverWait(driver, TIMEOUT)
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static WebElement waitForVisible(WebDriver driver, By locator) {
        return new WebDriverWait(driver, TIMEOUT)
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static boolean waitForUrlContains(WebDriver driver, String url) {
        return new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.urlContains(url));
    }

    public static boolean waitForInvisible(WebDriver driver, WebElement element) {
        return new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.invisibilityOf(element));
    }

    public static Alert waitForAlert(WebDriver driver) {
        return new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.alertIsPresent());
    }

    public static boolean waitForCondition(
            WebDriver driver, Function<WebDriver, Boolean> condition) {
        return new WebDriverWait(driver, TIMEOUT).until(condition);
    }
}
