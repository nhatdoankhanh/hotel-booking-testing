package com.navikenz.hotelbooking.dto.hotel;

import jakarta.validation.constraints.NotBlank;

public class HotelCreateRequest {

    @NotBlank
    private String city;

    @NotBlank
    private String hotelName;

    private String address;
    private String description;
    private Double avgRatePerDay = 0.0;
    private String email;
    private String phone1;
    private String phone2;
    private String website;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAvgRatePerDay() {
        return avgRatePerDay;
    }

    public void setAvgRatePerDay(Double avgRatePerDay) {
        this.avgRatePerDay = avgRatePerDay;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
