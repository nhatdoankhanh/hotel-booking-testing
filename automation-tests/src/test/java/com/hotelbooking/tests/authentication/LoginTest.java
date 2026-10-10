package com.hotelbooking.tests.authentication;

import com.hotelbooking.base.BaseJavaTest;
import com.hotelbooking.pages.HomePage;
import com.hotelbooking.pages.LoginPage;
import com.hotelbooking.pages.admin.AdminHomePape;
import com.hotelbooking.utils.ConfigReader;
import com.hotelbooking.utils.WaitUtil;

import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseJavaTest {
    @Test
    public void TC_AUTH_UI_004_loginCustomerSucessfully() {
        driver.get(ConfigReader.get("baseUrl") + "/login");

        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = new HomePage(driver);

        loginPage.loginCustomer(
                ConfigReader.get("customerEmail"), ConfigReader.get("customerPassword"));

        Assert.assertTrue(
                WaitUtil.waitForUrlContains(driver, "/hotels"), "Login failed: URL incorrect");

        Assert.assertTrue(homePage.isCustomerLogin(), "Login failed: Logout button not displayed");
    }

    @Test
    public void TC_AUTH_UI_005_loginAdminSucessfully() {
        driver.get(ConfigReader.get("baseUrl") + "/login");

        LoginPage loginPage = new LoginPage(driver);
        AdminHomePape adminHomePage = new AdminHomePape(driver);

        loginPage.loginAdmin(ConfigReader.get("adminEmail"), ConfigReader.get("adminPassword"));

        Assert.assertTrue(
                WaitUtil.waitForUrlContains(driver, "/admin"), "Login failed: URL incorrect");

        Assert.assertTrue(
                adminHomePage.isAdminLogin(), "Login failed: Logout button not displayed");
    }

    @Test
    public void TC_AUTH_UI_006_loginWithWrongPassword() {
        driver.get(ConfigReader.get("baseUrl") + "/login");

        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginCustomer(
                ConfigReader.get("customerEmail"), ConfigReader.get("wrongPassword"));

        Assert.assertTrue(
                WaitUtil.waitForUrlContains(driver, "/login"),
                "The system failed to keep the user on the Login page");
    }

    @Test
    public void TC_AUTH_UI_007_refreshShoulKeepCustomerSession() {
        driver.get(ConfigReader.get("baseUrl") + "/login");

        HomePage homepage = new HomePage(driver);
        LoginPage loginPage = new LoginPage(driver);

        loginPage.loginCustomer(
                ConfigReader.get("customerEmail"), ConfigReader.get("customerPassword"));

        Assert.assertTrue(
                homepage.isCustomerLogin() && WaitUtil.waitForUrlContains(driver, "hotels"),
                "Login failed before refresh");

        driver.navigate().refresh();

        Assert.assertTrue(
                homepage.isCustomerLogin() && WaitUtil.waitForUrlContains(driver, "hotels"),
                "Customer login was lost after refreshing the page");
    }
}
