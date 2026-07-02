package com.navikenz.hotelbooking.dto.room;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class RoomResponse {
    private Long roomId;
    private Long hotelId;
    private String roomNo;
    private String roomType;
    private Double ratePerDay;
    private Boolean isavailable;
    private String photo;
}
