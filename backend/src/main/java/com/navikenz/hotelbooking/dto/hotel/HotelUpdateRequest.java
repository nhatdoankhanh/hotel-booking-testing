package com.navikenz.hotelbooking.dto.hotel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelUpdateRequest {
    private String city;
    private String hotelName;
    private String address;
    private String description;
    private Double avgRatePerDay;
    private String email;
    private String phone1;
    private String phone2;
    private String website;
}
