package com.navikenz.hotelbooking.dto.room;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomUpdateRequest {
    private String roomNo;
    private String roomType;

    @Positive
    private Double ratePerDay;

    private Boolean isavailable;
    private String photo;
}
