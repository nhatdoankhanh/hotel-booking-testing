package com.navikenz.hotelbooking.dto.booking;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
    @FutureOrPresent
    private LocalDate bookedFrom;

    @NotNull
    private LocalDate bookedTo;

    @Min(1)
    private Integer noOfAdults = 1;

    @PositiveOrZero
    private Integer noOfChildren = 0;
}
