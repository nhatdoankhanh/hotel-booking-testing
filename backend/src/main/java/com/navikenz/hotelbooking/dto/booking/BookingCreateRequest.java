package com.navikenz.hotelbooking.dto.booking;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BookingCreateRequest {

    @NotNull
    private Long hotelId;

    @NotNull
    private Long roomId;

    @NotNull
    private LocalDate bookedFrom;

    @NotNull
    private LocalDate bookedTo;

    private Integer noOfAdults = 1;

    private Integer noOfChildren = 0;
}
