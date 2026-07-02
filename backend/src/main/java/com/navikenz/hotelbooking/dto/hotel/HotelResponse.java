package com.navikenz.hotelbooking.dto.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class HotelResponse {
    private Long hotelId;
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
