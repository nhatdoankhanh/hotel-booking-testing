package com.navikenz.hotelbooking.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class BookingResponse {
    private Long bookingId;
    private Long hotelId;
    private Long roomId;
    private Long userId;
    private LocalDate bookedFrom;
    private LocalDate bookedTo;
    private Integer noOfAdults;
    private Integer noOfChildren;
    private Double amount;
    private String status;
}
