package com.hotelbooking.tests.authentication;

import com.hotelbooking.base.BaseJavaTest;
import com.hotelbooking.pages.RegisterPage;
import com.hotelbooking.utils.ConfigReader;
import com.hotelbooking.utils.WaitUtil;

import org.testng.Assert;
import org.testng.annotations.Test;

public class RegisterTest extends BaseJavaTest {
    @Test
    public void TC_AUTH_UI_001_registerSuccessfully() {
        driver.get(ConfigReader.get("baseUrl") + "/register");

        RegisterPage registerPage = new RegisterPage(driver);
        String emailAddress = "auto_" + System.currentTimeMillis() + "@test.com";
        String fullName = ConfigReader.get("fullName");
        String password = ConfigReader.get("password");

        registerPage.register(fullName, emailAddress, password);

        Assert.assertTrue(
                WaitUtil.waitForUrlContains(driver, "login"),
                "Register failed: User was not redirected to Login page");
    }

    @Test
    public void TC_AUTH_UI_002_registerWithDuplicateEmail() {
        driver.get(ConfigReader.get("baseUrl") + "/register");
        RegisterPage registerPage = new RegisterPage(driver);
        registerPage.register(
                ConfigReader.get("fullName"),
                ConfigReader.get("customerEmail"),
                ConfigReader.get("password"));

        Assert.assertTrue(
                registerPage.isDuplicateEmail() && WaitUtil.waitForUrlContains(driver, "register"),
                "System incorrectly allowed registration with duplicate email");
    }
}
