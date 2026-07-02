package com.navikenz.hotelbooking.repository;

import com.navikenz.hotelbooking.entity.RoomDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<RoomDetails, Long> {
    List<RoomDetails> findByHotelId(Long hotelId);
    List<RoomDetails> findByHotelIdAndIsavailableTrue(Long hotelId);
    void deleteByHotelId(Long hotelId);
}
