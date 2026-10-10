package com.hotelbooking.hotel;

import com.hotelbooking.base.BaseJavaTest;
import com.hotelbooking.pages.HomePage;
import com.hotelbooking.utils.ConfigReader;

import org.testng.Assert;
import org.testng.annotations.Test;

public class HotelSearchTest extends BaseJavaTest {
    @Test
    public void TC_HOTEL_UI_001_displayHotelListSuccessfully() {
        driver.get(ConfigReader.get("baseUrl") + "/hotels");

        HomePage homePage = new HomePage(driver);

        Assert.assertTrue(homePage.isHotelListDisplayed(), "Hotel list is not displayed");

        Assert.assertTrue(homePage.getHotelCount() > 0, "Hotel list is empty");
    }

    @Test
    public void TC_HOTEL_UI_003_searchHotelByCitySuccessfully() {
        driver.get(ConfigReader.get("baseUrl") + "/hotels");

        HomePage homePage = new HomePage(driver);

        String hotelCity = ConfigReader.get("hotelCity");
        homePage.searchHotelByCity(hotelCity);

        Assert.assertTrue(
                homePage.areDisplayedCitiesContaining(hotelCity),
                "Search result contains incorrect city");
    }

    @Test
    public void TC_HOTEL_UI_004_searchHotelCaseInsensitive() {
        driver.get(ConfigReader.get("baseUrl") + "/hotels");

        HomePage homePage = new HomePage(driver);
        String hotelCity = ConfigReader.get("hotelCity");
        homePage.searchHotelByCity(hotelCity.toUpperCase());

        Assert.assertTrue(
                homePage.areDisplayedCitiesContaining(hotelCity.toLowerCase()),
                "City search is not case-insensitive");
    }

    @Test
    public void TC_HOTEL_UI_005_searchHotelBySubstring() {
        driver.get(ConfigReader.get("baseUrl") + "/hotels");

        HomePage homePage = new HomePage(driver);
        String hotelCity = ConfigReader.get("hotelCity");
        String subHotelCity = hotelCity.substring(0, 3);
        homePage.searchHotelByCity(subHotelCity);

        Assert.assertTrue(
                homePage.areDisplayedCitiesContaining(subHotelCity), "Search by substring failed");
    }
}
