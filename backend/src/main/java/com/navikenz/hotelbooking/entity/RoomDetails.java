package com.navikenz.hotelbooking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "room_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(nullable = false)
    private Long hotelId;

    @Column(nullable = false, length = 20)
    private String roomNo;

    @Column(nullable = false, length = 50)
    private String roomType;

    @Column(nullable = false)
    private Double ratePerDay;

    @Column
    @Builder.Default
    private Boolean isavailable = true;

    @Column(length = 500)
    private String photo;
}
