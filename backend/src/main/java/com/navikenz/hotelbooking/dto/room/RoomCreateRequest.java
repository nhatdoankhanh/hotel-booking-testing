package com.navikenz.hotelbooking.dto.room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @Positive
    private Double ratePerDay;

    private Boolean isavailable = true;

    private String photo;
}
