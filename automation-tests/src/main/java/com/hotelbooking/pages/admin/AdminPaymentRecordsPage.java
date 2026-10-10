package com.hotelbooking.pages.admin;

import com.hotelbooking.utils.WaitUtil;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AdminPaymentRecordsPage {
    private WebDriver driver;

    public AdminPaymentRecordsPage(WebDriver driver) {
        this.driver = driver;
    }

    public void open() {
        WaitUtil.waitForClickable(driver, By.cssSelector("a[href='/admin/payments']")).click();
        WaitUtil.waitForUrlContains(driver, "/admin/payments");
    }

    private By paymentRow(String bookingId) {
        return By.xpath("//table/tbody/tr[td[2][normalize-space()='#" + bookingId + "']]");
    }

    public boolean isPaymentDisplayed(String bookingId) {
        try {
            return WaitUtil.waitForVisible(driver, paymentRow(bookingId)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public double getAmount(String bookingId) {
        WebElement row = WaitUtil.waitForVisible(driver, paymentRow(bookingId));
        String text = row.findElement(By.xpath("./td[4]")).getText();

        return Double.parseDouble(text.replaceAll("[^0-9.]", ""));
    }

    public String getStatus(String bookingId) {
        WebElement row = WaitUtil.waitForVisible(driver, paymentRow(bookingId));
        return row.findElement(By.xpath("./td[7]")).getText().trim();
    }
}
