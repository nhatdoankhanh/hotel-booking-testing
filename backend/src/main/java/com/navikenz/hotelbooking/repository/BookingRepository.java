package com.navikenz.hotelbooking.repository;

import com.navikenz.hotelbooking.entity.BookingDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<BookingDetails, Long> {
    List<BookingDetails> findByUserId(Long userId);
}
