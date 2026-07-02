package com.navikenz.hotelbooking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "booking_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @Column(nullable = false)
    private Long hotelId;

    @Column(nullable = false)
    private Long roomId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDate bookedFrom;

    @Column(nullable = false)
    private LocalDate bookedTo;

    @Column(nullable = false)
    @Builder.Default
    private Integer noOfAdults = 1;

    @Column(nullable = false)
    @Builder.Default
    private Integer noOfChildren = 0;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "confirmed";
}
