package com.navikenz.hotelbooking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "hotels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hotelId;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 200)
    private String hotelName;

    @Column(length = 400)
    private String address;

    @Column(length = 1000)
    private String description;

    @Column
    @Builder.Default
    private Double avgRatePerDay = 0.0;

    @Column(length = 150)
    private String email;

    @Column(length = 20)
    private String phone1;

    @Column(length = 20)
    private String phone2;

    @Column(length = 200)
    private String website;
}
