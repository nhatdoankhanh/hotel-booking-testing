package com.navikenz.hotelbooking.dto.room;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomUpdateRequest {
    private String roomNo;
    private String roomType;
    private Double ratePerDay;
    private Boolean isavailable;
    private String photo;
}
