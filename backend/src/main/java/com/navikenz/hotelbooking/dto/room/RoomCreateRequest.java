package com.navikenz.hotelbooking.dto.room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomCreateRequest {

    @NotNull
    private Long hotelId;

    @NotBlank
    private String roomNo;

    @NotBlank
    private String roomType;

    @NotNull
    private Double ratePerDay;

    private Boolean isavailable = true;

    private String photo;
}
