package com.hotelbooking.hotel;

import com.hotelbooking.base.BaseJavaTest;
import com.hotelbooking.pages.LoginPage;
import com.hotelbooking.pages.admin.AdminHomePape;
import com.hotelbooking.pages.admin.HotelManagementPage;
import com.hotelbooking.utils.ConfigReader;
import com.hotelbooking.utils.WaitUtil;

import org.testng.Assert;
import org.testng.annotations.Test;

public class HotelMagagementTest extends BaseJavaTest {
    private HotelManagementPage loginAdminAndOpenHotelManagement() {
        driver.get(ConfigReader.get("baseUrl") + "/login");

        LoginPage loginPage = new LoginPage(driver);

        AdminHomePape adminHomePage = new AdminHomePape(driver);

        loginPage.loginAdmin(ConfigReader.get("adminEmail"), ConfigReader.get("adminPassword"));

        Assert.assertTrue(adminHomePage.isAdminLogin(), "Admin login failed");

        adminHomePage.openHotelManagement();

        Assert.assertTrue(
                WaitUtil.waitForUrlContains(driver, "/admin/hotels"),
                "Hotel Management page was not opened");

        return new HotelManagementPage(driver);
    }

    @Test
    public void TC_HOTEL_UI_006_addHotelSuccessfully() {
        HotelManagementPage hotelPage = loginAdminAndOpenHotelManagement();

        String hotelName = ConfigReader.get("hotelName1");
        String hotelCity = ConfigReader.get("hotelCity1");
        String avgRate = ConfigReader.get("avgRate1");

        hotelPage.addHotel(hotelName, hotelCity, avgRate);

        Assert.assertTrue(
                hotelPage.isHotelDisplayed(hotelName), "Hotel was not created successfully");

        Assert.assertEquals(
                hotelPage.getHotelCity(hotelName), hotelCity, "Hotel city is incorrect");

        Assert.assertTrue(
                hotelPage.getHotelRate(hotelName).contains(avgRate), "Hotel rate is incorrect");
    }

    @Test
    public void TC_HOTEL_UI_007_updateHotelSuccessfully() {
        HotelManagementPage hotelPage = loginAdminAndOpenHotelManagement();

        String hotelName = ConfigReader.get("hotelName2");
        String hotelCity = ConfigReader.get("hotelCity2");
        String avgRate = ConfigReader.get("avgRate2");

        String newHotelName = hotelName + " update";
        String newHotelCity = hotelCity + " update";
        String newAvgRate = String.valueOf(Integer.parseInt(avgRate) + 10);

        hotelPage.addHotel(hotelName, hotelCity, avgRate);

        Assert.assertTrue(hotelPage.isHotelDisplayed(hotelName), "Hotel setup failed");

        hotelPage.updateHotel(hotelName, newHotelName, newHotelCity, newAvgRate);

        Assert.assertTrue(
                hotelPage.isHotelDisplayed(newHotelName), "Updated hotel was not displayed");

        Assert.assertEquals(
                hotelPage.getHotelCity(newHotelName), newHotelCity, "Hotel city was not updated");

        Assert.assertTrue(
                hotelPage.getHotelRate(newHotelName).contains(newAvgRate),
                "Hotel rate was not updated");
    }

    @Test
    public void TC_HOTEL_UI_008_deleteHotelSuccessfully() {
        HotelManagementPage hotelPage = loginAdminAndOpenHotelManagement();

        String hotelName = ConfigReader.get("hotelName2");
        String hotelCity = ConfigReader.get("hotelCity2");
        String avgRate = ConfigReader.get("avgRate2");

        hotelPage.addHotel(hotelName, hotelCity, avgRate);

        Assert.assertTrue(hotelPage.isHotelDisplayed(hotelName), "Hotel setup failed");

        hotelPage.deleteHotel(hotelName);

        Assert.assertTrue(
                hotelPage.waitUntilHotelRemoved(hotelName), "Hotel was not deleted successfully");
    }
}
