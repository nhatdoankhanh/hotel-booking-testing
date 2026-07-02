package com.navikenz.hotelbooking.dto.hotel;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}
